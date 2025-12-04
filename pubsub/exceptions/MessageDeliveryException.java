package pubsub.exceptions;

/**
 * Exception thrown when message delivery fails.
 */
public class MessageDeliveryException extends PubSubException {
    
    private final String messageId;
    private final String subscriberId;
    
    public MessageDeliveryException(String messageId, String subscriberId, Throwable cause) {
        super("Failed to deliver message " + messageId + " to subscriber " + subscriberId, cause);
        this.messageId = messageId;
        this.subscriberId = subscriberId;
    }
    
    public String getMessageId() {
        return messageId;
    }
    
    public String getSubscriberId() {
        return subscriberId;
    }
}



