package linkedin.models;

import linkedin.enums.ConnectionStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public class Connection {
    private final String id;
    private final String requesterId;
    private final String receiverId;
    private ConnectionStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    public Connection(String requesterId, String receiverId) {
        this.id = UUID.randomUUID().toString();
        this.requesterId = requesterId;
        this.receiverId = receiverId;
        this.status = ConnectionStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public String getRequesterId() { return requesterId; }
    public String getReceiverId() { return receiverId; }
    public ConnectionStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    
    // Methods
    public void accept() {
        this.status = ConnectionStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void decline() {
        this.status = ConnectionStatus.DECLINED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void block() {
        this.status = ConnectionStatus.BLOCKED;
        this.updatedAt = LocalDateTime.now();
    }
    
    public boolean involvesUser(String userId) {
        return requesterId.equals(userId) || receiverId.equals(userId);
    }
    
    public String getOtherUserId(String userId) {
        if (requesterId.equals(userId)) return receiverId;
        if (receiverId.equals(userId)) return requesterId;
        return null;
    }
    
    @Override
    public String toString() {
        return "Connection{id='" + id + "', requester='" + requesterId + 
               "', receiver='" + receiverId + "', status=" + status + "}";
    }
}



