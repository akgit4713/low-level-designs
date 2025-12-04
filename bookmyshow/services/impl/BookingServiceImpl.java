package bookmyshow.services.impl;

import bookmyshow.enums.BookingStatus;
import bookmyshow.enums.PaymentMethod;
import bookmyshow.enums.PaymentStatus;
import bookmyshow.exceptions.*;
import bookmyshow.models.*;
import bookmyshow.observers.BookingObserver;
import bookmyshow.repositories.BookingRepository;
import bookmyshow.repositories.ShowRepository;
import bookmyshow.repositories.UserRepository;
import bookmyshow.services.BookingService;
import bookmyshow.services.PaymentService;
import bookmyshow.strategies.pricing.PricingStrategy;
import bookmyshow.strategies.refund.RefundStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Implementation of BookingService with concurrent booking handling.
 */
public class BookingServiceImpl implements BookingService {
    
    private static final int SEAT_LOCK_DURATION_MINUTES = 10;
    
    private final BookingRepository bookingRepository;
    private final ShowRepository showRepository;
    private final UserRepository userRepository;
    private final PaymentService paymentService;
    private final PricingStrategy pricingStrategy;
    private final RefundStrategy refundStrategy;
    private final List<BookingObserver> observers;
    private final ReentrantLock bookingLock;

    public BookingServiceImpl(BookingRepository bookingRepository,
                             ShowRepository showRepository,
                             UserRepository userRepository,
                             PaymentService paymentService,
                             PricingStrategy pricingStrategy,
                             RefundStrategy refundStrategy) {
        this.bookingRepository = bookingRepository;
        this.showRepository = showRepository;
        this.userRepository = userRepository;
        this.paymentService = paymentService;
        this.pricingStrategy = pricingStrategy;
        this.refundStrategy = refundStrategy;
        this.observers = new ArrayList<>();
        this.bookingLock = new ReentrantLock();
    }

    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }

    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }

    @Override
    public Booking initiateBooking(String userId, String showId, List<String> seatIds) {
        bookingLock.lock();
        try {
            // Validate user exists
            userRepository.findById(userId)
                .orElseThrow(() -> new EntityNotFoundException("User", userId));
            
            // Get show and validate
            Show show = showRepository.findById(showId)
                .orElseThrow(() -> new ShowNotFoundException(showId));
            
            // Check and lock seats
            List<ShowSeat> seatsToBook = new ArrayList<>();
            for (String seatId : seatIds) {
                ShowSeat showSeat = show.getShowSeat(seatId);
                if (showSeat == null) {
                    throw new EntityNotFoundException("Seat", seatId);
                }
                
                if (!showSeat.isAvailable()) {
                    throw new SeatNotAvailableException(seatId, showId);
                }
                
                seatsToBook.add(showSeat);
            }
            
            // Lock all seats
            for (ShowSeat showSeat : seatsToBook) {
                if (!showSeat.lock(userId, SEAT_LOCK_DURATION_MINUTES)) {
                    // Rollback already locked seats
                    for (ShowSeat lockedSeat : seatsToBook) {
                        if (lockedSeat.isLockedByUser(userId)) {
                            lockedSeat.unlock();
                        }
                    }
                    throw new SeatNotAvailableException(showSeat.getSeat().getId(), showId);
                }
            }
            
            // Calculate total price
            BigDecimal totalAmount = pricingStrategy.calculateTotalPrice(show, seatsToBook);
            
            // Create booking
            Booking booking = new Booking(userId, showId, seatIds, totalAmount);
            bookingRepository.save(booking);
            
            // Update user's booking list
            User user = userRepository.findById(userId).get();
            user.addBooking(booking.getId());
            userRepository.save(user);
            
            // Save show with updated seat status
            showRepository.save(show);
            
            System.out.println("Booking initiated: " + booking.getId());
            System.out.println("Seats locked for " + SEAT_LOCK_DURATION_MINUTES + " minutes");
            System.out.println("Total amount: ₹" + totalAmount);
            
            return booking;
            
        } finally {
            bookingLock.unlock();
        }
    }

    @Override
    public Booking confirmBooking(String bookingId, PaymentMethod paymentMethod) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
        
        // Check if booking has expired
        if (booking.isExpired()) {
            booking.expire();
            bookingRepository.save(booking);
            releaseBookingSeats(booking);
            notifyBookingExpired(booking);
            throw new BookingExpiredException(bookingId);
        }
        
        // Check booking status
        if (booking.getStatus() != BookingStatus.INITIATED && 
            booking.getStatus() != BookingStatus.PENDING) {
            throw new InvalidOperationException(
                "Cannot confirm booking in status: " + booking.getStatus());
        }
        
        // Process payment
        booking.markPending();
        bookingRepository.save(booking);
        
        Payment payment = paymentService.processPayment(
            bookingId, 
            booking.getTotalAmount(), 
            paymentMethod
        );
        
        if (payment.getStatus() == PaymentStatus.COMPLETED) {
            // Confirm booking
            booking.setPaymentId(payment.getId());
            booking.confirm();
            
            // Update seat status to BOOKED
            Show show = showRepository.findById(booking.getShowId()).get();
            for (String seatId : booking.getSeatIds()) {
                ShowSeat showSeat = show.getShowSeat(seatId);
                showSeat.book(bookingId);
            }
            showRepository.save(show);
            bookingRepository.save(booking);
            
            // Notify observers
            notifyBookingConfirmed(booking);
            
            return booking;
        } else {
            // Payment failed - release seats
            releaseBookingSeats(booking);
            booking.cancel();
            bookingRepository.save(booking);
            throw new PaymentFailedException("Payment failed: " + payment.getFailureReason());
        }
    }

    @Override
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> new BookingNotFoundException(bookingId));
        
        if (!booking.canBeCancelled()) {
            throw new InvalidOperationException(
                "Cannot cancel booking in status: " + booking.getStatus());
        }
        
        Show show = showRepository.findById(booking.getShowId()).get();
        
        // Calculate refund
        BigDecimal refundAmount = refundStrategy.calculateRefundAmount(booking, show);
        
        // Process refund if applicable
        if (refundAmount.compareTo(BigDecimal.ZERO) > 0) {
            paymentService.processRefund(booking.getPaymentId());
            booking.refund();
        } else {
            booking.cancel();
        }
        
        // Release seats
        releaseBookingSeats(booking);
        bookingRepository.save(booking);
        
        // Notify observers
        notifyBookingCancelled(booking);
        
        System.out.println("Booking cancelled. Refund amount: ₹" + refundAmount);
        
        return booking;
    }

    @Override
    public Optional<Booking> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public List<Booking> getUserBookings(String userId) {
        return bookingRepository.findByUserId(userId);
    }

    @Override
    public List<Booking> getShowBookings(String showId) {
        return bookingRepository.findByShowId(showId);
    }

    @Override
    public void processExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findExpiredBookings();
        
        for (Booking booking : expiredBookings) {
            booking.expire();
            releaseBookingSeats(booking);
            bookingRepository.save(booking);
            notifyBookingExpired(booking);
            System.out.println("Expired booking processed: " + booking.getId());
        }
    }

    private void releaseBookingSeats(Booking booking) {
        showRepository.findById(booking.getShowId()).ifPresent(show -> {
            for (String seatId : booking.getSeatIds()) {
                ShowSeat showSeat = show.getShowSeat(seatId);
                if (showSeat != null) {
                    showSeat.unlock();
                }
            }
            showRepository.save(show);
        });
    }

    private void notifyBookingConfirmed(Booking booking) {
        for (BookingObserver observer : observers) {
            observer.onBookingConfirmed(booking);
        }
    }

    private void notifyBookingCancelled(Booking booking) {
        for (BookingObserver observer : observers) {
            observer.onBookingCancelled(booking);
        }
    }

    private void notifyBookingExpired(Booking booking) {
        for (BookingObserver observer : observers) {
            observer.onBookingExpired(booking);
        }
    }
}



