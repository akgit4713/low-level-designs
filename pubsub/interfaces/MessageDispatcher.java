package pubsub.interfaces;

import pubsub.models.Message;
import pubsub.models.Subscription;

import java.util.Set;

/**
 * Strategy interface for message delivery.
 * Different implementations can provide sync, async, batched, or priority-based delivery.
 * 
 * Following Strategy Pattern: Encapsulates delivery algorithm.
 * Following DIP: High-level modules depend on this abstraction.
 *
 * @param <T> The type of message payload to dispatch
 */
public interface MessageDispatcher<T> {
    
    /**
     * Dispatches a message to all provided subscriptions.
     *
     * @param message The message to dispatch
     * @param subscriptions The set of subscriptions to deliver to
     */
    void dispatch(Message<T> message, Set<Subscription<T>> subscriptions);
    
    /**
     * Gracefully shuts down the dispatcher, completing pending deliveries.
     */
    void shutdown();
    
    /**
     * Immediately shuts down the dispatcher.
     */
    void shutdownNow();
    
    /**
     * Checks if the dispatcher is still running.
     *
     * @return true if the dispatcher can accept new dispatch requests
     */
    boolean isRunning();
}



