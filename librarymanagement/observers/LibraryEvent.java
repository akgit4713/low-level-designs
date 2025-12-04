package librarymanagement.observers;

import java.time.LocalDateTime;

/**
 * Represents an event that occurred in the library system.
 */
public class LibraryEvent {
    
    public enum EventType {
        BOOK_BORROWED,
        BOOK_RETURNED,
        BOOK_OVERDUE,
        MEMBER_REGISTERED,
        MEMBER_SUSPENDED,
        FINE_ISSUED,
        FINE_PAID,
        BOOK_ADDED,
        BOOK_REMOVED
    }

    private final EventType type;
    private final String entityId;
    private final String message;
    private final LocalDateTime timestamp;
    private final Object payload;

    public LibraryEvent(EventType type, String entityId, String message) {
        this(type, entityId, message, null);
    }

    public LibraryEvent(EventType type, String entityId, String message, Object payload) {
        this.type = type;
        this.entityId = entityId;
        this.message = message;
        this.timestamp = LocalDateTime.now();
        this.payload = payload;
    }

    public EventType getType() {
        return type;
    }

    public String getEntityId() {
        return entityId;
    }

    public String getMessage() {
        return message;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public Object getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - %s: %s", timestamp, type, entityId, message);
    }
}



