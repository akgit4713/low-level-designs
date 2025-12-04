package concertbooking.services;

import concertbooking.models.Booking;
import concertbooking.models.Ticket;
import concertbooking.observers.BookingObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing bookings
 */
public interface BookingService {
    
    /**
     * Initiate a booking by holding seats for a user
     * @param userId User ID
     * @param concertId Concert ID
     * @param seatIds List of seat IDs to book
     * @return Created booking in PENDING status
     */
    Booking initiateBooking(String userId, String concertId, List<String> seatIds);
    
    /**
     * Confirm a pending booking after payment
     * @param bookingId Booking ID
     * @param paymentId Payment ID
     * @return Confirmed booking
     */
    Booking confirmBooking(String bookingId, String paymentId);
    
    /**
     * Cancel a booking
     * @param bookingId Booking ID
     * @return Cancelled booking
     */
    Booking cancelBooking(String bookingId);
    
    /**
     * Get booking by ID
     */
    Optional<Booking> getBooking(String bookingId);
    
    /**
     * Get all bookings for a user
     */
    List<Booking> getUserBookings(String userId);
    
    /**
     * Get all bookings for a concert
     */
    List<Booking> getConcertBookings(String concertId);
    
    /**
     * Generate tickets for a confirmed booking
     */
    List<Ticket> generateTickets(String bookingId);
    
    /**
     * Clean up expired pending bookings
     */
    void cleanupExpiredBookings();
    
    /**
     * Add booking observer
     */
    void addObserver(BookingObserver observer);
    
    /**
     * Remove booking observer
     */
    void removeObserver(BookingObserver observer);
}



