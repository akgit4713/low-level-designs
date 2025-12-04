package pubsub.impl;

import pubsub.interfaces.MessageDispatcher;
import pubsub.models.Message;
import pubsub.models.Subscription;

import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Synchronous message dispatcher - delivers messages in the calling thread.
 * Useful for:
 * - Testing (predictable execution order)
 * - Simple use cases with few subscribers
 * - Debugging
 * 
 * Note: Blocking - not recommended for production with many subscribers.
 *
 * @param <T> The type of message payload
 */
public class SyncMessageDispatcher<T> implements MessageDispatcher<T> {
    
    private final AtomicBoolean running;
    private final boolean continueOnError;
    
    public SyncMessageDispatcher() {
        this(true);
    }
    
    /**
     * Creates a sync dispatcher.
     *
     * @param continueOnError If true, continues delivering to other subscribers even if one fails
     */
    public SyncMessageDispatcher(boolean continueOnError) {
        this.running = new AtomicBoolean(true);
        this.continueOnError = continueOnError;
    }
    
    @Override
    public void dispatch(Message<T> message, Set<Subscription<T>> subscriptions) {
        if (!running.get()) {
            throw new IllegalStateException("Dispatcher has been shut down");
        }
        
        if (subscriptions == null || subscriptions.isEmpty()) {
            return;
        }
        
        for (Subscription<T> subscription : subscriptions) {
            if (!subscription.isActive()) {
                continue;
            }
            
            try {
                subscription.getSubscriber().onMessage(message);
            } catch (Exception e) {
                try {
                    subscription.getSubscriber().onError(message, e);
                } catch (Exception errorHandlerException) {
                    System.err.println("Error handler failed for subscriber " + 
                            subscription.getSubscriber().getId() + ": " + errorHandlerException.getMessage());
                }
                
                if (!continueOnError) {
                    throw new RuntimeException("Message delivery failed to subscriber: " + 
                            subscription.getSubscriber().getId(), e);
                }
            }
        }
    }
    
    @Override
    public void shutdown() {
        running.set(false);
    }
    
    @Override
    public void shutdownNow() {
        running.set(false);
    }
    
    @Override
    public boolean isRunning() {
        return running.get();
    }
}



