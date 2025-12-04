package pubsub.subscribers;

import pubsub.interfaces.Subscriber;
import pubsub.models.Message;

import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * A subscriber that logs received messages to console.
 * Useful for debugging and demonstration.
 *
 * @param <T> The type of message payload
 */
public class LoggingSubscriber<T> implements Subscriber<T> {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_INSTANT;
    
    private final String id;
    private final String name;
    
    public LoggingSubscriber() {
        this("LoggingSubscriber-" + UUID.randomUUID().toString().substring(0, 8));
    }
    
    public LoggingSubscriber(String name) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
    }
    
    @Override
    public void onMessage(Message<T> message) {
        System.out.printf("[%s] %s received message on topic '%s': %s%n",
                FORMATTER.format(message.getTimestamp()),
                name,
                message.getTopic().getName(),
                message.getPayload());
    }
    
    @Override
    public String getId() {
        return id;
    }
    
    @Override
    public void onError(Message<T> message, Throwable error) {
        System.err.printf("[ERROR] %s failed to process message %s: %s%n",
                name, message.getId(), error.getMessage());
    }
    
    @Override
    public String toString() {
        return "LoggingSubscriber{name='" + name + "', id='" + id + "'}";
    }
}



