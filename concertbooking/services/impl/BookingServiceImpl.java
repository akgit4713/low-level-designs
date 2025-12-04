package concertbooking.services.impl;

import concertbooking.enums.ConcertStatus;
import concertbooking.exceptions.*;
import concertbooking.models.*;
import concertbooking.observers.BookingObserver;
import concertbooking.repositories.BookingRepository;
import concertbooking.repositories.ConcertRepository;
import concertbooking.repositories.UserRepository;
import concertbooking.services.BookingService;
import concertbooking.strategies.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of BookingService with concurrency handling
 */
public class BookingServiceImpl implements BookingService {
    
    private static final int HOLD_DURATION_MINUTES = 15;
    private static final int MAX_SEATS_PER_BOOKING = 10;
    
    private final BookingRepository bookingRepository;
    private final ConcertRepository concertRepository;
    private final UserRepository userRepository;
    private final PricingStrategy pricingStrategy;
    private final List<BookingObserver> observers = new CopyOnWriteArrayList<>();
    
    public BookingServiceImpl(BookingRepository bookingRepository,
                              ConcertRepository concertRepository,
                              UserRepository userRepository,
                              PricingStrategy pricingStrategy) {
        this.bookingRepository = bookingRepository;
        this.concertRepository = concertRepository;
        this.userRepository = userRepository;
        this.pricingStrategy = pricingStrategy;
    }
    
    @Override
    public Booking initiateBooking(String userId, String concertId, List<String> seatIds) {
        // Validate input
        if (seatIds == null || seatIds.isEmpty()) {
            throw BookingException.invalidSeats();
        }
        
        if (seatIds.size() > MAX_SEATS_PER_BOOKING) {
            throw BookingException.maxSeatsExceeded(MAX_SEATS_PER_BOOKING);
        }
        
        // Get concert
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        
        // Check if concert is bookable
        if (!concert.isBookable()) {
            throw BookingException.concertNotBookable(concertId);
        }
        
        // Try to hold all seats atomically
        List<Seat> heldSeats = new ArrayList<>();
        List<String> failedSeats = new ArrayList<>();
        
        for (String seatId : seatIds) {
            Optional<Seat> seatOpt = concert.getSeat(seatId);
            if (seatOpt.isEmpty()) {
                failedSeats.add(seatId);
                continue;
            }
            
            Seat seat = seatOpt.get();
            if (seat.tryHold(userId, HOLD_DURATION_MINUTES)) {
                heldSeats.add(seat);
            } else {
                failedSeats.add(seatId);
            }
        }
        
        // If any seats failed to hold, release the ones we held and throw exception
        if (!failedSeats.isEmpty()) {
            heldSeats.forEach(seat -> seat.releaseHold(userId));
            throw SeatNotAvailableException.seatsAlreadyBooked(failedSeats);
        }
        
        // Calculate total price
        BigDecimal totalAmount = pricingStrategy.calculatePrice(concert, heldSeats);
        
        // Create booking
        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking booking = Booking.builder()
            .id(bookingId)
            .concertId(concertId)
            .userId(userId)
            .seatIds(seatIds)
            .totalAmount(totalAmount)
            .expiresAt(LocalDateTime.now().plusMinutes(HOLD_DURATION_MINUTES))
            .build();
        
        bookingRepository.save(booking);
        
        // Notify observers
        notifyBookingCreated(booking);
        
        System.out.println("[BOOKING] Created booking " + bookingId + " for " + seatIds.size() 
            + " seat(s), Total: $" + totalAmount);
        
        return booking;
    }
    
    @Override
    public Booking confirmBooking(String bookingId, String paymentId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> BookingException.notFound(bookingId));
        
        // Check if booking is still valid
        if (booking.isExpired()) {
            handleExpiredBooking(booking);
            throw BookingException.expired(bookingId);
        }
        
        if (booking.isConfirmed()) {
            throw BookingException.alreadyConfirmed(bookingId);
        }
        
        if (booking.isCancelled()) {
            throw BookingException.alreadyCancelled(bookingId);
        }
        
        // Get concert and confirm seat bookings
        Concert concert = concertRepository.findById(booking.getConcertId())
            .orElseThrow(() -> ConcertNotFoundException.byId(booking.getConcertId()));
        
        for (String seatId : booking.getSeatIds()) {
            concert.getSeat(seatId).ifPresent(seat -> {
                if (!seat.confirmBooking(booking.getUserId())) {
                    throw new ConcertBookingException("Failed to confirm seat: " + seatId);
                }
            });
        }
        
        // Confirm booking
        booking.confirm(paymentId);
        bookingRepository.save(booking);
        
        // Check if concert is now sold out
        if (concert.isSoldOut()) {
            concert.setStatus(ConcertStatus.SOLD_OUT);
            concertRepository.save(concert);
            System.out.println("[CONCERT] " + concert.getName() + " is now SOLD OUT!");
        }
        
        // Notify observers
        notifyBookingConfirmed(booking);
        
        System.out.println("[BOOKING] Confirmed booking " + bookingId);
        
        return booking;
    }
    
    @Override
    public Booking cancelBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> BookingException.notFound(bookingId));
        
        if (booking.isCancelled()) {
            throw BookingException.alreadyCancelled(bookingId);
        }
        
        // Release seats
        Concert concert = concertRepository.findById(booking.getConcertId())
            .orElseThrow(() -> ConcertNotFoundException.byId(booking.getConcertId()));
        
        for (String seatId : booking.getSeatIds()) {
            concert.getSeat(seatId).ifPresent(seat -> {
                if (booking.isConfirmed()) {
                    seat.releaseBooking(booking.getUserId());
                } else {
                    seat.releaseHold(booking.getUserId());
                }
            });
        }
        
        // Cancel booking
        booking.cancel();
        bookingRepository.save(booking);
        
        // If concert was sold out, update status
        if (concert.getStatus() == ConcertStatus.SOLD_OUT && !concert.isSoldOut()) {
            concert.setStatus(ConcertStatus.ON_SALE);
            concertRepository.save(concert);
        }
        
        // Notify observers
        notifyBookingCancelled(booking);
        
        System.out.println("[BOOKING] Cancelled booking " + bookingId);
        
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
    public List<Booking> getConcertBookings(String concertId) {
        return bookingRepository.findByConcertId(concertId);
    }
    
    @Override
    public List<Ticket> generateTickets(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
            .orElseThrow(() -> BookingException.notFound(bookingId));
        
        if (!booking.isConfirmed()) {
            throw new ConcertBookingException("Cannot generate tickets for unconfirmed booking");
        }
        
        Concert concert = concertRepository.findById(booking.getConcertId())
            .orElseThrow(() -> ConcertNotFoundException.byId(booking.getConcertId()));
        
        User user = userRepository.findById(booking.getUserId())
            .orElseThrow(() -> new ConcertBookingException("User not found: " + booking.getUserId()));
        
        List<Ticket> tickets = new ArrayList<>();
        
        for (String seatId : booking.getSeatIds()) {
            Seat seat = concert.getSeat(seatId)
                .orElseThrow(() -> new ConcertBookingException("Seat not found: " + seatId));
            
            Ticket ticket = Ticket.builder()
                .id("TKT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .bookingId(bookingId)
                .concertId(concert.getId())
                .concertName(concert.getName())
                .artistName(concert.getArtist())
                .venueName(concert.getVenue().getName())
                .concertDateTime(concert.getDateTime())
                .seatId(seatId)
                .seatLabel(seat.getSeatLabel())
                .sectionName(seat.getSectionType().getDisplayName())
                .price(concert.getSectionPrice(seat.getSectionType()))
                .userId(user.getId())
                .userName(user.getName())
                .build();
            
            tickets.add(ticket);
        }
        
        return tickets;
    }
    
    @Override
    public void cleanupExpiredBookings() {
        List<Booking> expiredBookings = bookingRepository.findExpiredPendingBookings();
        
        for (Booking booking : expiredBookings) {
            handleExpiredBooking(booking);
        }
        
        if (!expiredBookings.isEmpty()) {
            System.out.println("[CLEANUP] Processed " + expiredBookings.size() + " expired bookings");
        }
    }
    
    private void handleExpiredBooking(Booking booking) {
        // Release seats
        concertRepository.findById(booking.getConcertId()).ifPresent(concert -> {
            for (String seatId : booking.getSeatIds()) {
                concert.getSeat(seatId).ifPresent(Seat::releaseExpiredHold);
            }
        });
        
        booking.markExpired();
        bookingRepository.save(booking);
        
        notifyBookingExpired(booking);
    }
    
    @Override
    public void addObserver(BookingObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(BookingObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyBookingCreated(Booking booking) {
        observers.forEach(o -> o.onBookingCreated(booking));
    }
    
    private void notifyBookingConfirmed(Booking booking) {
        observers.forEach(o -> o.onBookingConfirmed(booking));
    }
    
    private void notifyBookingCancelled(Booking booking) {
        observers.forEach(o -> o.onBookingCancelled(booking));
    }
    
    private void notifyBookingExpired(Booking booking) {
        observers.forEach(o -> o.onBookingExpired(booking));
    }
}



