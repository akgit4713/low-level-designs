package airline.exceptions;

/**
 * Exception for flight-related errors.
 */
public class FlightException extends AirlineException {
    
    public FlightException(String message) {
        super(message);
    }

    public FlightException(String message, Throwable cause) {
        super(message, cause);
    }
}



