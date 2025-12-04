package linkedin.models;

import java.time.LocalDateTime;
import java.util.UUID;

public class Message {
    private final String id;
    private final String senderId;
    private final String receiverId;
    private final String conversationId;
    private final String content;
    private final LocalDateTime timestamp;
    private boolean isRead;
    
    public Message(String senderId, String receiverId, String conversationId, String content) {
        this.id = UUID.randomUUID().toString();
        this.senderId = senderId;
        this.receiverId = receiverId;
        this.conversationId = conversationId;
        this.content = content;
        this.timestamp = LocalDateTime.now();
        this.isRead = false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getSenderId() { return senderId; }
    public String getReceiverId() { return receiverId; }
    public String getConversationId() { return conversationId; }
    public String getContent() { return content; }
    public LocalDateTime getTimestamp() { return timestamp; }
    public boolean isRead() { return isRead; }
    
    // Methods
    public void markAsRead() {
        this.isRead = true;
    }
    
    @Override
    public String toString() {
        return "Message{id='" + id + "', from='" + senderId + 
               "', to='" + receiverId + "', read=" + isRead + "}";
    }
}



