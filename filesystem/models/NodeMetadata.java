package filesystem.models;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Metadata associated with a file system node.
 * Contains timestamps and size information for detailed listing.
 */
public class NodeMetadata {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("MMM dd HH:mm");
    
    private final LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    
    public NodeMetadata() {
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = this.createdAt;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getModifiedAt() {
        return modifiedAt;
    }
    
    public void updateModifiedTime() {
        this.modifiedAt = LocalDateTime.now();
    }
    
    public String getFormattedModifiedTime() {
        return modifiedAt.format(FORMATTER);
    }
}

