package airline.services;

import airline.models.Booking;
import airline.models.Flight;
import airline.models.Passenger;
import airline.observers.BookingObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for booking management operations.
 */
public interface BookingService {
    
    /**
     * Creates a new booking.
     */
    Booking createBooking(Flight flight, List<PassengerSeatSelection> passengers);
    
    /**
     * Gets a booking by ID.
     */
    Optional<Booking> getBooking(String bookingId);
    
    /**
     * Gets a booking by PNR.
     */
    Optional<Booking> getBookingByPnr(String pnr);
    
    /**
     * Confirms a booking after payment.
     */
    void confirmBooking(String bookingId);
    
    /**
     * Cancels a booking.
     */
    void cancelBooking(String bookingId, String reason);
    
    /**
     * Gets all bookings for a flight.
     */
    List<Booking> getBookingsForFlight(String flightNumber);
    
    /**
     * Adds a booking observer.
     */
    void addObserver(BookingObserver observer);
    
    /**
     * Removes a booking observer.
     */
    void removeObserver(BookingObserver observer);
    
    /**
     * Record for passenger with seat selection.
     */
    record PassengerSeatSelection(Passenger passenger, String seatNumber) {}
}



