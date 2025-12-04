package pubsub.exceptions;

/**
 * Base exception for all pub-sub related errors.
 */
public class PubSubException extends RuntimeException {
    
    public PubSubException(String message) {
        super(message);
    }
    
    public PubSubException(String message, Throwable cause) {
        super(message, cause);
    }
}



