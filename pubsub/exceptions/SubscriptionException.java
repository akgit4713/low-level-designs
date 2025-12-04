package pubsub.exceptions;

/**
 * Exception thrown for subscription-related errors.
 */
public class SubscriptionException extends PubSubException {
    
    public SubscriptionException(String message) {
        super(message);
    }
    
    public SubscriptionException(String message, Throwable cause) {
        super(message, cause);
    }
}



