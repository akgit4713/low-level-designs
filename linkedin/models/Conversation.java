package linkedin.models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Conversation {
    private final String id;
    private final List<String> participantIds;
    private final List<Message> messages;
    private LocalDateTime lastActivityAt;
    
    public Conversation(String userId1, String userId2) {
        this.id = UUID.randomUUID().toString();
        this.participantIds = new ArrayList<>();
        this.participantIds.add(userId1);
        this.participantIds.add(userId2);
        this.messages = new ArrayList<>();
        this.lastActivityAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() { return id; }
    public List<String> getParticipantIds() { return new ArrayList<>(participantIds); }
    public List<Message> getMessages() { return new ArrayList<>(messages); }
    public LocalDateTime getLastActivityAt() { return lastActivityAt; }
    
    // Methods
    public void addMessage(Message message) {
        this.messages.add(message);
        this.lastActivityAt = LocalDateTime.now();
    }
    
    public boolean hasParticipant(String userId) {
        return participantIds.contains(userId);
    }
    
    public boolean hasParticipants(String userId1, String userId2) {
        return participantIds.contains(userId1) && participantIds.contains(userId2);
    }
    
    public String getOtherParticipant(String userId) {
        for (String participantId : participantIds) {
            if (!participantId.equals(userId)) {
                return participantId;
            }
        }
        return null;
    }
    
    public int getUnreadCount(String userId) {
        return (int) messages.stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.isRead())
                .count();
    }
    
    @Override
    public String toString() {
        return "Conversation{id='" + id + "', participants=" + participantIds + 
               ", messageCount=" + messages.size() + "}";
    }
}



