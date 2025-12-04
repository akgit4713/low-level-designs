package bookmyshow.services;

import bookmyshow.enums.PaymentMethod;
import bookmyshow.models.Booking;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for booking management.
 * Handles seat selection, locking, payment, and confirmation.
 */
public interface BookingService {
    
    /**
     * Initiate a booking by locking selected seats.
     * @param userId User making the booking
     * @param showId Show ID
     * @param seatIds List of seat IDs to book
     * @return Initiated booking with locked seats
     */
    Booking initiateBooking(String userId, String showId, List<String> seatIds);
    
    /**
     * Confirm booking after successful payment.
     * @param bookingId Booking ID to confirm
     * @param paymentMethod Payment method to use
     * @return Confirmed booking
     */
    Booking confirmBooking(String bookingId, PaymentMethod paymentMethod);
    
    /**
     * Cancel a booking.
     * @param bookingId Booking ID to cancel
     * @return Cancelled booking
     */
    Booking cancelBooking(String bookingId);
    
    /**
     * Get booking by ID.
     * @param bookingId Booking ID
     * @return Optional booking
     */
    Optional<Booking> getBooking(String bookingId);
    
    /**
     * Get all bookings for a user.
     * @param userId User ID
     * @return List of user's bookings
     */
    List<Booking> getUserBookings(String userId);
    
    /**
     * Get all bookings for a show.
     * @param showId Show ID
     * @return List of show's bookings
     */
    List<Booking> getShowBookings(String showId);
    
    /**
     * Process expired bookings and release locked seats.
     */
    void processExpiredBookings();
}



