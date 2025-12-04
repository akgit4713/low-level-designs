package airline.exceptions;

/**
 * Base exception for all airline-related exceptions.
 */
public class AirlineException extends RuntimeException {
    
    public AirlineException(String message) {
        super(message);
    }

    public AirlineException(String message, Throwable cause) {
        super(message, cause);
    }
}



