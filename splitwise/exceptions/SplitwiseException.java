package splitwise.exceptions;

/**
 * Base exception class for all Splitwise-related exceptions.
 */
public class SplitwiseException extends RuntimeException {
    
    public SplitwiseException(String message) {
        super(message);
    }
    
    public SplitwiseException(String message, Throwable cause) {
        super(message, cause);
    }
}



