package bookmyshow.exceptions;

/**
 * Thrown when a booking is not found.
 */
public class BookingNotFoundException extends BookMyShowException {
    
    public BookingNotFoundException(String bookingId) {
        super(String.format("Booking not found with ID: %s", bookingId));
    }
}



