package socialnetwork.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a like on a post.
 */
public class Like {
    private final String id;
    private final String postId;
    private final String userId;
    private final LocalDateTime createdAt;

    public Like(String postId, String userId) {
        this.id = UUID.randomUUID().toString();
        this.postId = postId;
        this.userId = userId;
        this.createdAt = LocalDateTime.now();
    }

    public Like(String id, String postId, String userId, LocalDateTime createdAt) {
        this.id = id;
        this.postId = postId;
        this.userId = userId;
        this.createdAt = createdAt;
    }

    // Getters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getUserId() { return userId; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Like like = (Like) o;
        return Objects.equals(id, like.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Like{id='" + id + "', postId='" + postId + "', userId='" + userId + "'}";
    }
}



