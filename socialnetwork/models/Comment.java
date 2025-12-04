package socialnetwork.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a comment on a post.
 */
public class Comment {
    private final String id;
    private final String postId;
    private final String authorId;
    private String content;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;

    private Comment(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.postId = builder.postId;
        this.authorId = builder.authorId;
        this.content = builder.content;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.isDeleted = false;
    }

    // Getters
    public String getId() { return id; }
    public String getPostId() { return postId; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }

    // Setters
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Comment comment = (Comment) o;
        return Objects.equals(id, comment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Comment{id='" + id + "', postId='" + postId + "', authorId='" + authorId + "'}";
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String postId;
        private String authorId;
        private String content;
        private LocalDateTime createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder postId(String postId) { this.postId = postId; return this; }
        public Builder authorId(String authorId) { this.authorId = authorId; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Comment build() {
            if (postId == null || postId.isBlank()) {
                throw new IllegalArgumentException("Post ID is required");
            }
            if (authorId == null || authorId.isBlank()) {
                throw new IllegalArgumentException("Author ID is required");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Content is required");
            }
            return new Comment(this);
        }
    }
}



