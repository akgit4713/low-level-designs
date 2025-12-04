package linkedin.observers;

import linkedin.models.Notification;

/**
 * Observer interface for notification delivery.
 * Different implementations handle different delivery channels.
 */
public interface NotificationObserver {
    
    /**
     * Called when a notification is created
     * @param notification The notification to deliver
     */
    void onNotification(Notification notification);
    
    /**
     * Get the name of this notification channel
     */
    String getChannelName();
}



