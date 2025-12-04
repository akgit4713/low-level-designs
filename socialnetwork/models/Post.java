package socialnetwork.models;

import socialnetwork.enums.PostType;
import socialnetwork.enums.PrivacyLevel;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a post created by a user.
 * Can contain text, images, videos, or links.
 */
public class Post {
    private final String id;
    private final String authorId;
    private String content;
    private PostType type;
    private String mediaUrl;
    private PrivacyLevel privacyLevel;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean isDeleted;
    private final List<String> likeIds;
    private final List<String> commentIds;

    private Post(Builder builder) {
        this.id = builder.id != null ? builder.id : UUID.randomUUID().toString();
        this.authorId = builder.authorId;
        this.content = builder.content;
        this.type = builder.type != null ? builder.type : PostType.TEXT;
        this.mediaUrl = builder.mediaUrl;
        this.privacyLevel = builder.privacyLevel != null ? builder.privacyLevel : PrivacyLevel.FRIENDS_ONLY;
        this.createdAt = builder.createdAt != null ? builder.createdAt : LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.isDeleted = false;
        this.likeIds = new ArrayList<>();
        this.commentIds = new ArrayList<>();
    }

    // Getters
    public String getId() { return id; }
    public String getAuthorId() { return authorId; }
    public String getContent() { return content; }
    public PostType getType() { return type; }
    public String getMediaUrl() { return mediaUrl; }
    public PrivacyLevel getPrivacyLevel() { return privacyLevel; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public boolean isDeleted() { return isDeleted; }
    public List<String> getLikeIds() { return Collections.unmodifiableList(likeIds); }
    public List<String> getCommentIds() { return Collections.unmodifiableList(commentIds); }
    public int getLikeCount() { return likeIds.size(); }
    public int getCommentCount() { return commentIds.size(); }

    // Setters
    public void setContent(String content) {
        this.content = content;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPrivacyLevel(PrivacyLevel privacyLevel) {
        this.privacyLevel = privacyLevel;
        this.updatedAt = LocalDateTime.now();
    }

    public void setMediaUrl(String mediaUrl) {
        this.mediaUrl = mediaUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void markAsDeleted() {
        this.isDeleted = true;
        this.updatedAt = LocalDateTime.now();
    }

    public void addLike(String likeId) {
        if (!likeIds.contains(likeId)) {
            likeIds.add(likeId);
        }
    }

    public void removeLike(String likeId) {
        likeIds.remove(likeId);
    }

    public void addComment(String commentId) {
        commentIds.add(commentId);
    }

    public void removeComment(String commentId) {
        commentIds.remove(commentId);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Post post = (Post) o;
        return Objects.equals(id, post.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Post{id='" + id + "', authorId='" + authorId + "', type=" + type + 
               ", likes=" + getLikeCount() + ", comments=" + getCommentCount() + "}";
    }

    // Builder Pattern
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String authorId;
        private String content;
        private PostType type;
        private String mediaUrl;
        private PrivacyLevel privacyLevel;
        private LocalDateTime createdAt;

        public Builder id(String id) { this.id = id; return this; }
        public Builder authorId(String authorId) { this.authorId = authorId; return this; }
        public Builder content(String content) { this.content = content; return this; }
        public Builder type(PostType type) { this.type = type; return this; }
        public Builder mediaUrl(String mediaUrl) { this.mediaUrl = mediaUrl; return this; }
        public Builder privacyLevel(PrivacyLevel privacyLevel) { this.privacyLevel = privacyLevel; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }

        public Post build() {
            if (authorId == null || authorId.isBlank()) {
                throw new IllegalArgumentException("Author ID is required");
            }
            if (content == null || content.isBlank()) {
                throw new IllegalArgumentException("Content is required");
            }
            return new Post(this);
        }
    }
}



