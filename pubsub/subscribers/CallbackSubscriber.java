package pubsub.subscribers;

import pubsub.interfaces.Subscriber;
import pubsub.models.Message;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

/**
 * A subscriber that invokes a callback function for each message.
 * Provides flexibility for custom message handling.
 *
 * @param <T> The type of message payload
 */
public class CallbackSubscriber<T> implements Subscriber<T> {
    
    private final String id;
    private final Consumer<Message<T>> callback;
    private final Consumer<Throwable> errorCallback;
    
    /**
     * Creates a callback subscriber with message handler only.
     *
     * @param callback The callback to invoke for each message
     */
    public CallbackSubscriber(Consumer<Message<T>> callback) {
        this(callback, null);
    }
    
    /**
     * Creates a callback subscriber with message and error handlers.
     *
     * @param callback The callback to invoke for each message
     * @param errorCallback The callback to invoke on errors
     */
    public CallbackSubscriber(Consumer<Message<T>> callback, Consumer<Throwable> errorCallback) {
        this.id = UUID.randomUUID().toString();
        this.callback = Objects.requireNonNull(callback, "Callback cannot be null");
        this.errorCallback = errorCallback;
    }
    
    @Override
    public void onMessage(Message<T> message) {
        callback.accept(message);
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void onError(Message<T> message, Throwable error) {
        if (errorCallback != null) {
            errorCallback.accept(error);
        } else {
            Subscriber.super.onError(message, error);
        }
    }
    
    @Override
    public String toString() {
        return "CallbackSubscriber{id='" + id + "'}";
    }
}



