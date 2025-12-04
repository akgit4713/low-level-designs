package airline.services.impl;

import airline.enums.BookingStatus;
import airline.enums.SeatClass;
import airline.exceptions.BookingException;
import airline.exceptions.SeatException;
import airline.models.Booking;
import airline.models.Flight;
import airline.models.Passenger;
import airline.models.Seat;
import airline.observers.BookingObserver;
import airline.repositories.BookingRepository;
import airline.services.BookingService;
import airline.strategies.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of BookingService.
 */
public class BookingServiceImpl implements BookingService {
    
    private final BookingRepository bookingRepository;
    private final PricingStrategy pricingStrategy;
    private final List<BookingObserver> observers = new ArrayList<>();

    public BookingServiceImpl(BookingRepository bookingRepository, PricingStrategy pricingStrategy) {
        this.bookingRepository = bookingRepository;
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public Booking createBooking(Flight flight, List<PassengerSeatSelection> passengers) {
        if (passengers == null || passengers.isEmpty()) {
            throw new BookingException("At least one passenger is required");
        }

        // Validate and book seats
        List<SeatBookingInfo> seatInfos = new ArrayList<>();
        for (PassengerSeatSelection selection : passengers) {
            Seat seat = flight.getSeat(selection.seatNumber())
                    .orElseThrow(() -> new SeatException("Seat " + selection.seatNumber() + " not found"));
            
            if (!seat.isAvailable()) {
                throw new SeatException("Seat " + selection.seatNumber() + " is not available");
            }
            
            seatInfos.add(new SeatBookingInfo(selection.passenger(), selection.seatNumber(), seat.getSeatClass()));
        }

        // Book seats
        for (SeatBookingInfo info : seatInfos) {
            if (!flight.bookSeat(info.seatNumber, info.passenger.getId())) {
                // Rollback already booked seats
                for (SeatBookingInfo booked : seatInfos) {
                    if (booked.seatNumber.equals(info.seatNumber)) break;
                    flight.releaseSeat(booked.seatNumber);
                }
                throw new SeatException("Failed to book seat " + info.seatNumber);
            }
        }

        // Calculate total price
        BigDecimal totalPrice = BigDecimal.ZERO;
        for (SeatBookingInfo info : seatInfos) {
            totalPrice = totalPrice.add(pricingStrategy.calculatePrice(flight, info.seatClass));
        }

        // Create booking
        String bookingId = "BKG-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        Booking.Builder builder = Booking.builder()
                .id(bookingId)
                .flight(flight)
                .totalAmount(totalPrice);
        
        for (SeatBookingInfo info : seatInfos) {
            builder.addPassenger(info.passenger, info.seatNumber);
        }
        
        Booking booking = builder.build();
        bookingRepository.save(booking);
        
        notifyBookingCreated(booking);
        
        return booking;
    }

    @Override
    public Optional<Booking> getBooking(String bookingId) {
        return bookingRepository.findById(bookingId);
    }

    @Override
    public Optional<Booking> getBookingByPnr(String pnr) {
        return bookingRepository.findByPnr(pnr);
    }

    @Override
    public void confirmBooking(String bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found: " + bookingId));
        
        if (booking.getStatus() != BookingStatus.PENDING) {
            throw new BookingException("Can only confirm pending bookings");
        }
        
        booking.setStatus(BookingStatus.CONFIRMED);
        bookingRepository.save(booking);
        
        notifyBookingConfirmed(booking);
    }

    @Override
    public void cancelBooking(String bookingId, String reason) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new BookingException("Booking not found: " + bookingId));
        
        booking.cancel(reason);
        bookingRepository.save(booking);
        
        notifyBookingCancelled(booking);
    }

    @Override
    public List<Booking> getBookingsForFlight(String flightNumber) {
        return bookingRepository.findByFlightNumber(flightNumber);
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
        for (BookingObserver observer : observers) {
            observer.onBookingCreated(booking);
        }
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

    private record SeatBookingInfo(Passenger passenger, String seatNumber, SeatClass seatClass) {}
}



