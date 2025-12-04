package socialnetwork.repositories;

import socialnetwork.models.Notification;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Notification data access.
 */
public interface NotificationRepository {
    
    Notification save(Notification notification);
    
    Optional<Notification> findById(String id);
    
    List<Notification> findByUserId(String userId);
    
    List<Notification> findUnreadByUserId(String userId);
    
    int countUnreadByUserId(String userId);
    
    void markAsRead(String id);
    
    void markAllAsReadForUser(String userId);
    
    void delete(String id);
}



