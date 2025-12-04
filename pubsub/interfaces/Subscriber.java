package pubsub.interfaces;

import pubsub.models.Message;

/**
 * Interface for message subscribers.
 * Implementations define how to handle received messages.
 * 
 * Following ISP: Single method interface for message handling.
 *
 * @param <T> The type of message payload this subscriber can handle
 */
public interface Subscriber<T> {
    
    /**
     * Called when a message is delivered to this subscriber.
     * Implementations should be non-blocking or handle blocking internally.
     *
     * @param message The message received from the subscribed topic
     */
    void onMessage(Message<T> message);
    
    /**
     * Returns a unique identifier for this subscriber.
     * Used for logging, debugging, and subscription management.
     *
     * @return Unique subscriber identifier
     */
    String getId();
    
    /**
     * Called when an error occurs during message delivery.
     * Default implementation logs the error.
     *
     * @param message The message that failed to deliver
     * @param error The exception that occurred
     */
    default void onError(Message<T> message, Throwable error) {
        System.err.println("Error delivering message " + message.getId() + 
                          " to subscriber " + getId() + ": " + error.getMessage());
    }
}



