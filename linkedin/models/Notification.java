package linkedin.models;

import linkedin.enums.NotificationType;
import java.time.LocalDateTime;
import java.util.UUID;

public class Notification {
    private final String id;
    private final String userId;
    private final NotificationType type;
    private final String content;
    private final String referenceId; // ID of related entity (connection, message, job, etc.)
    private boolean isRead;
    private final LocalDateTime createdAt;
    
    public Notification(String userId, NotificationType type, String content, String referenceId) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.type = type;
        this.content = content;
        this.referenceId = referenceId;
        this.isRead = false;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getUserId() { return userId; }
    public NotificationType getType() { return type; }
    public String getContent() { return content; }
    public String getReferenceId() { return referenceId; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    
    // Methods
    public void markAsRead() {
        this.isRead = true;
    }
    
    @Override
    public String toString() {
        return "Notification{id='" + id + "', userId='" + userId + 
               "', type=" + type + ", read=" + isRead + "}";
    }
}



