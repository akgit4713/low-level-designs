package socialnetwork.models;

import socialnetwork.enums.NotificationType;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a notification for a user.
 */
public class Notification {
    private final String id;
    private final String userId;
    private final String actorId;
    private final NotificationType type;
    private final String referenceId; // ID of related entity (post, comment, etc.)
    private final String message;
    private boolean isRead;
    private final LocalDateTime createdAt;

    private Notification(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.userId = builder.userId;
        this.actorId = builder.actorId;
        this.type = builder.type;
        this.referenceId = builder.referenceId;
        this.message = builder.message;
        this.isRead = false;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public String getActorId() { return actorId; }
    public NotificationType getType() { return type; }
    public String getReferenceId() { return referenceId; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    // Mark as read
    public void markAsRead() {
        this.isRead = true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Notification that = (Notification) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Notification{id='" + id + "', type=" + type + 
               ", message='" + message + "', read=" + isRead + "}";
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String userId;
        private String actorId;
        private NotificationType type;
        private String referenceId;
        private String message;
        private LocalDateTime createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder actorId(String actorId) { this.actorId = actorId; return this; }
        public Builder type(NotificationType type) { this.type = type; return this; }
        public Builder referenceId(String referenceId) { this.referenceId = referenceId; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Notification build() {
            if (userId == null || userId.isBlank()) {
                throw new IllegalArgumentException("User ID is required");
            }
            if (type == null) {
                throw new IllegalArgumentException("Notification type is required");
            }
            if (message == null || message.isBlank()) {
                throw new IllegalArgumentException("Message is required");
            }
            return new Notification(this);
        }
    }
}



