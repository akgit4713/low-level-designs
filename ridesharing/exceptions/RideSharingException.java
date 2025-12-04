package ridesharing.exceptions;

/**
 * Base exception for all ride-sharing related errors.
 */
public class RideSharingException extends RuntimeException {
    
    public RideSharingException(String message) {
        super(message);
    }
    
    public RideSharingException(String message, Throwable cause) {
        super(message, cause);
    }
}



