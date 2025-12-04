package pubsub.models;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Immutable message object that flows through the pub-sub system.
 * Contains payload, topic reference, unique ID, and timestamp.
 *
 * @param <T> The type of the message payload
 */
public final class Message<T> {
    
    private final String id;
    private final Topic topic;
    private final T payload;
    private final Instant timestamp;
    private final String publisherId;
    
    private Message(Builder<T> builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.topic = Objects.requireNonNull(builder.topic, "Topic cannot be null");
        this.payload = Objects.requireNonNull(builder.payload, "Payload cannot be null");
        this.timestamp = builder.timestamp != null ? builder.timestamp : Instant.now();
        this.publisherId = builder.publisherId;
    }
    
    public String getId() {
        return id;
    }
    
    public Topic getTopic() {
        return topic;
    }
    
    public T getPayload() {
        return payload;
    }
    
    public Instant getTimestamp() {
        return timestamp;
    }
    
    public String getPublisherId() {
        return publisherId;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Message<?> message = (Message<?>) o;
        return Objects.equals(id, message.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Message{" +
                "id='" + id + '\'' +
                ", topic=" + topic +
                ", payload=" + payload +
                ", timestamp=" + timestamp +
                ", publisherId='" + publisherId + '\'' +
                '}';
    }
    
    // Builder Pattern for flexible message construction
    public static <T> Builder<T> builder() {
        return new Builder<>();
    }
    
    public static class Builder<T> {
        private String id;
        private Topic topic;
        private T payload;
        private Instant timestamp;
        private String publisherId;
        
        public Builder<T> id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder<T> topic(Topic topic) {
            this.topic = topic;
            return this;
        }
        
        public Builder<T> topic(String topicName) {
            this.topic = new Topic(topicName);
            return this;
        }
        
        public Builder<T> payload(T payload) {
            this.payload = payload;
            return this;
        }
        
        public Builder<T> timestamp(Instant timestamp) {
            this.timestamp = timestamp;
            return this;
        }
        
        public Builder<T> publisherId(String publisherId) {
            this.publisherId = publisherId;
            return this;
        }
        
        public Message<T> build() {
            return new Message<>(this);
        }
    }
}



