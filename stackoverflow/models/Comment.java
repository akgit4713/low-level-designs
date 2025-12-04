package stackoverflow.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a comment on a question or answer.
 */
public class Comment {
    private final String id;
    private final String content;
    private final User author;
    private final LocalDateTime createdAt;

    public Comment(String content, User author) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.content = content;
        this.author = author;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() { return id; }
    public String getContent() { return content; }
    public User getAuthor() { return author; }
    public LocalDateTime getCreatedAt() { return createdAt; }

    @Override
    public String toString() {
        return String.format("Comment by %s: %s", author.getUsername(), 
            content.length() > 50 ? content.substring(0, 50) + "..." : content);
    }
}



