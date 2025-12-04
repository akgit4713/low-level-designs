package linkedin.services;

import linkedin.exceptions.MessagingException;
import linkedin.exceptions.UserNotFoundException;
import linkedin.factories.NotificationFactory;
import linkedin.models.Conversation;
import linkedin.models.Message;
import linkedin.models.Notification;
import linkedin.models.User;
import linkedin.repositories.ConnectionRepository;
import linkedin.repositories.MessageRepository;
import linkedin.repositories.UserRepository;

import java.util.List;

/**
 * Service for messaging between connected users.
 */
public class MessagingService {
    
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;
    private final ConnectionRepository connectionRepository;
    private final NotificationService notificationService;
    
    public MessagingService(MessageRepository messageRepository,
                           UserRepository userRepository,
                           ConnectionRepository connectionRepository,
                           NotificationService notificationService) {
        this.messageRepository = messageRepository;
        this.userRepository = userRepository;
        this.connectionRepository = connectionRepository;
        this.notificationService = notificationService;
    }
    
    public Message sendMessage(String senderId, String receiverId, String content) {
        validateMessageRequest(senderId, receiverId, content);
        
        // Get or create conversation
        Conversation conversation = getOrCreateConversation(senderId, receiverId);
        
        // Create and save message
        Message message = new Message(senderId, receiverId, conversation.getId(), content);
        messageRepository.saveMessage(message);
        
        // Add to conversation
        conversation.addMessage(message);
        messageRepository.saveConversation(conversation);
        
        // Send notification
        User sender = userRepository.findById(senderId).get();
        User receiver = userRepository.findById(receiverId).get();
        Notification notification = NotificationFactory.createMessageNotification(sender, receiver, message);
        notificationService.notify(notification);
        
        return message;
    }
    
    public List<Conversation> getConversations(String userId) {
        validateUser(userId);
        return messageRepository.findConversationsByUserId(userId);
    }
    
    public Conversation getConversation(String userId, String otherUserId) {
        validateUser(userId);
        validateUser(otherUserId);
        
        return messageRepository.findConversationByParticipants(userId, otherUserId)
                .orElseThrow(() -> new MessagingException("No conversation found with this user"));
    }
    
    public List<Message> getMessages(String conversationId, String userId) {
        Conversation conversation = messageRepository.findConversationById(conversationId)
                .orElseThrow(() -> new MessagingException("Conversation not found"));
        
        if (!conversation.hasParticipant(userId)) {
            throw new MessagingException("You are not a participant in this conversation");
        }
        
        return messageRepository.findMessagesByConversationId(conversationId);
    }
    
    public void markMessageAsRead(String messageId, String userId) {
        Message message = messageRepository.findMessageById(messageId)
                .orElseThrow(() -> new MessagingException("Message not found"));
        
        if (!message.getReceiverId().equals(userId)) {
            throw new MessagingException("Cannot mark this message as read");
        }
        
        message.markAsRead();
        messageRepository.saveMessage(message);
    }
    
    public void markConversationAsRead(String conversationId, String userId) {
        Conversation conversation = messageRepository.findConversationById(conversationId)
                .orElseThrow(() -> new MessagingException("Conversation not found"));
        
        if (!conversation.hasParticipant(userId)) {
            throw new MessagingException("You are not a participant in this conversation");
        }
        
        List<Message> messages = messageRepository.findMessagesByConversationId(conversationId);
        messages.stream()
                .filter(m -> m.getReceiverId().equals(userId) && !m.isRead())
                .forEach(m -> {
                    m.markAsRead();
                    messageRepository.saveMessage(m);
                });
    }
    
    public List<Message> getUnreadMessages(String userId) {
        validateUser(userId);
        return messageRepository.findUnreadMessagesForUser(userId);
    }
    
    public int getUnreadCount(String userId) {
        validateUser(userId);
        return messageRepository.countUnreadMessagesForUser(userId);
    }
    
    private Conversation getOrCreateConversation(String userId1, String userId2) {
        return messageRepository.findConversationByParticipants(userId1, userId2)
                .orElseGet(() -> {
                    Conversation newConversation = new Conversation(userId1, userId2);
                    return messageRepository.saveConversation(newConversation);
                });
    }
    
    private void validateMessageRequest(String senderId, String receiverId, String content) {
        validateUser(senderId);
        validateUser(receiverId);
        
        if (senderId.equals(receiverId)) {
            throw new MessagingException("Cannot send message to yourself");
        }
        
        if (content == null || content.trim().isEmpty()) {
            throw new MessagingException("Message content cannot be empty");
        }
        
        // Check if users are connected
        if (!connectionRepository.areConnected(senderId, receiverId)) {
            throw new MessagingException("Can only send messages to connections");
        }
    }
    
    private void validateUser(String userId) {
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }
    }
}



