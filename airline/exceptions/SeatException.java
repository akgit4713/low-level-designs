package airline.exceptions;

/**
 * Exception for seat-related errors.
 */
public class SeatException extends AirlineException {
    
    public SeatException(String message) {
        super(message);
    }

    public SeatException(String message, Throwable cause) {
        super(message, cause);
    }
}



