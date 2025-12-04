package socialnetwork.observers;

import socialnetwork.models.Notification;
import socialnetwork.repositories.NotificationRepository;

/**
 * Observer that stores notifications in the repository for in-app display.
 */
public class InAppNotificationObserver implements NotificationObserver {
    
    private final NotificationRepository notificationRepository;

    public InAppNotificationObserver(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @Override
    public void onNotification(Notification notification) {
        notificationRepository.save(notification);
        System.out.println("[IN-APP] Notification saved: " + notification.getMessage());
    }

    @Override
    public String getChannelName() {
        return "In-App";
    }
}



