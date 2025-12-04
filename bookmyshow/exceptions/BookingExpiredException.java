package bookmyshow.exceptions;

/**
 * Thrown when a booking has expired.
 */
public class BookingExpiredException extends BookMyShowException {
    
    public BookingExpiredException(String bookingId) {
        super(String.format("Booking %s has expired. Please start a new booking.", bookingId));
    }
}



