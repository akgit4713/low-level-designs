package linkedin.repositories;

import linkedin.enums.NotificationType;
import linkedin.models.Notification;
import java.util.List;

public interface NotificationRepository extends Repository<Notification, String> {
    List<Notification> findByUserId(String userId);
    List<Notification> findUnreadByUserId(String userId);
    List<Notification> findByUserIdAndType(String userId, NotificationType type);
    int countUnreadByUserId(String userId);
    void markAllAsReadForUser(String userId);
}



