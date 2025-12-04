package linkedin.repositories.impl;

import linkedin.models.Conversation;
import linkedin.models.Message;
import linkedin.repositories.MessageRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class InMemoryMessageRepository implements MessageRepository {
    
    private final Map<String, Message> messages = new ConcurrentHashMap<>();
    private final Map<String, Conversation> conversations = new ConcurrentHashMap<>();
    
    @Override
    public Message saveMessage(Message message) {
        messages.put(message.getId(), message);
        return message;
    }
    
    @Override
    public Conversation saveConversation(Conversation conversation) {
        conversations.put(conversation.getId(), conversation);
        return conversation;
    }
    
    @Override
    public Optional<Message> findMessageById(String id) {
        return Optional.ofNullable(messages.get(id));
    }
    
    @Override
    public Optional<Conversation> findConversationById(String id) {
        return Optional.ofNullable(conversations.get(id));
    }
    
    @Override
    public Optional<Conversation> findConversationByParticipants(String userId1, String userId2) {
        return conversations.values().stream()
                .filter(c -> c.hasParticipants(userId1, userId2))
                .findFirst();
    }
    
    @Override
    public List<Conversation> findConversationsByUserId(String userId) {
        return conversations.values().stream()
                .filter(c -> c.hasParticipant(userId))
                .sorted((c1, c2) -> c2.getLastActivityAt().compareTo(c1.getLastActivityAt()))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Message> findMessagesByConversationId(String conversationId) {
        return messages.values().stream()
                .filter(m -> m.getConversationId().equals(conversationId))
                .sorted(Comparator.comparing(Message::getTimestamp))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Message> findUnreadMessagesForUser(String userId) {
        return messages.values().stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.isRead())
                .sorted(Comparator.comparing(Message::getTimestamp).reversed())
                .collect(Collectors.toList());
    }
    
    @Override
    public int countUnreadMessagesForUser(String userId) {
        return (int) messages.values().stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.isRead())
                .count();
    }
}



