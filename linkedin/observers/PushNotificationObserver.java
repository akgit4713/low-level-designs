package linkedin.observers;

import linkedin.models.Notification;

/**
 * Observer that sends push notifications.
 * In production, this would integrate with a push notification service.
 */
public class PushNotificationObserver implements NotificationObserver {
    
    @Override
    public void onNotification(Notification notification) {
        // In production, this would send push notifications via FCM/APNS
        System.out.println("[PUSH] Notification for user " + notification.getUserId() + 
                          ": " + notification.getContent());
    }
    
    @Override
    public String getChannelName() {
        return "PUSH";
    }
}



