package pubsub.models;

import pubsub.interfaces.Subscriber;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a subscription binding between a subscriber and a topic.
 * Contains metadata about when the subscription was created.
 *
 * @param <T> The type of messages this subscription handles
 */
public final class Subscription<T> {
    
    private final String id;
    private final Topic topic;
    private final Subscriber<T> subscriber;
    private final Instant createdAt;
    private volatile boolean active;
    
    public Subscription(Topic topic, Subscriber<T> subscriber) {
        this.id = UUID.randomUUID().toString();
        this.topic = Objects.requireNonNull(topic, "Topic cannot be null");
        this.subscriber = Objects.requireNonNull(subscriber, "Subscriber cannot be null");
        this.createdAt = Instant.now();
        this.active = true;
    }
    
    public String getId() {
        return id;
    }
    
    public Topic getTopic() {
        return topic;
    }
    
    public Subscriber<T> getSubscriber() {
        return subscriber;
    }
    
    public Instant getCreatedAt() {
        return createdAt;
    }
    
    public boolean isActive() {
        return active;
    }
    
    public void deactivate() {
        this.active = false;
    }
    
    public void activate() {
        this.active = true;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subscription<?> that = (Subscription<?>) o;
        return Objects.equals(id, that.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Subscription{" +
                "id='" + id + '\'' +
                ", topic=" + topic +
                ", subscriberId='" + subscriber.getId() + '\'' +
                ", active=" + active +
                '}';
    }
}



