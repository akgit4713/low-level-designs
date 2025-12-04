package linkedin.repositories;

import linkedin.models.Conversation;
import linkedin.models.Message;
import java.util.List;
import java.util.Optional;

public interface MessageRepository {
    Message saveMessage(Message message);
    Conversation saveConversation(Conversation conversation);
    Optional<Message> findMessageById(String id);
    Optional<Conversation> findConversationById(String id);
    Optional<Conversation> findConversationByParticipants(String userId1, String userId2);
    List<Conversation> findConversationsByUserId(String userId);
    List<Message> findMessagesByConversationId(String conversationId);
    List<Message> findUnreadMessagesForUser(String userId);
    int countUnreadMessagesForUser(String userId);
}



