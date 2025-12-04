package socialnetwork.observers;

import socialnetwork.models.Notification;

/**
 * Observer that sends push notifications.
 * In production, this would integrate with FCM, APNs, etc.
 */
public class PushNotificationObserver implements NotificationObserver {

    @Override
    public void onNotification(Notification notification) {
        // In production, this would use a push notification service
        System.out.println("[PUSH] Sending push to user " + notification.getUserId() + 
                          ": " + notification.getMessage());
    }

    @Override
    public String getChannelName() {
        return "Push";
    }
}



