package socialnetwork.models;

import socialnetwork.enums.FriendshipStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a friend request/connection between two users.
 */
public class FriendRequest {
    private final String id;
    private final String senderId;
    private final String receiverId;
    private FriendshipStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public FriendRequest(String senderId, String receiverId) {
        this.id = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = FriendshipStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
    }

    public FriendRequest(String id, String senderId, String receiverId, 
                         FriendshipStatus status, LocalDateTime createdAt) {
        this.id = id;
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.status = status;
        this.createdAt = createdAt;
        this.updatedAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public FriendshipStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Status updates
    public void accept() {
        this.status = FriendshipStatus.ACCEPTED;
        this.updatedAt = LocalDateTime.now();
    }

    public void decline() {
        this.status = FriendshipStatus.DECLINED;
        this.updatedAt = LocalDateTime.now();
    }

    public void block() {
        this.status = FriendshipStatus.BLOCKED;
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPending() {
        return status == FriendshipStatus.PENDING;
    }

    public boolean isAccepted() {
        return status == FriendshipStatus.ACCEPTED;
    }

    public boolean involvesUser(String userId) {
        return senderId.equals(userId) || receiverId.equals(userId);
    }

    public String getOtherUser(String userId) {
        if (senderId.equals(userId)) return receiverId;
        if (receiverId.equals(userId)) return senderId;
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FriendRequest that = (FriendRequest) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "FriendRequest{id='" + id + "', sender='" + senderId + 
               "', receiver='" + receiverId + "', status=" + status + "}";
    }
}



