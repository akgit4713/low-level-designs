package pubsub.subscribers;

import pubsub.interfaces.Subscriber;
import pubsub.models.Message;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * A decorator subscriber that filters messages before forwarding.
 * Demonstrates Decorator Pattern for extending subscriber behavior.
 *
 * @param <T> The type of message payload
 */
public class FilteringSubscriber<T> implements Subscriber<T> {
    
    private final Subscriber<T> delegate;
    private final Predicate<Message<T>> filter;
    
    /**
     * Creates a filtering subscriber.
     *
     * @param delegate The underlying subscriber to forward messages to
     * @param filter Predicate that returns true for messages to forward
     */
    public FilteringSubscriber(Subscriber<T> delegate, Predicate<Message<T>> filter) {
        this.delegate = Objects.requireNonNull(delegate, "Delegate cannot be null");
        this.filter = Objects.requireNonNull(filter, "Filter cannot be null");
    }
    
    @Override
    public void onMessage(Message<T> message) {
        if (filter.test(message)) {
            delegate.onMessage(message);
        }
    }
    
    @Override
    public String getId() {
        return delegate.getId();
    }
    
    @Override
    public void onError(Message<T> message, Throwable error) {
        delegate.onError(message, error);
    }
    
    /**
     * Gets the underlying delegate subscriber.
     *
     * @return The delegate subscriber
     */
    public Subscriber<T> getDelegate() {
        return delegate;
    }
    
    @Override
    public String toString() {
        return "FilteringSubscriber{delegate=" + delegate + "}";
    }
}



