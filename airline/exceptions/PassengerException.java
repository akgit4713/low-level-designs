package airline.exceptions;

/**
 * Exception for passenger-related errors.
 */
public class PassengerException extends AirlineException {
    
    public PassengerException(String message) {
        super(message);
    }

    public PassengerException(String message, Throwable cause) {
        super(message, cause);
    }
}



