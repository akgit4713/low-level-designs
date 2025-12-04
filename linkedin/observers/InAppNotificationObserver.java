package linkedin.observers;

import linkedin.models.Notification;
import linkedin.repositories.NotificationRepository;

/**
 * Observer that stores notifications for in-app display.
 */
public class InAppNotificationObserver implements NotificationObserver {
    
    private final NotificationRepository notificationRepository;
    
    public InAppNotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }
    
    @Override
    public void onNotification(Notification notification) {
        // Store notification for in-app display
        notificationRepository.save(notification);
        System.out.println("[IN-APP] Notification stored for user " + notification.getUserId() + 
                          ": " + notification.getContent());
    }
    
    @Override
    public String getChannelName() {
        return "IN_APP";
    }
}



