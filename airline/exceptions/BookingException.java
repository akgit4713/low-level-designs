package airline.exceptions;

/**
 * Exception for booking-related errors.
 */
public class BookingException extends AirlineException {
    
    public BookingException(String message) {
        super(message);
    }

    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
}



