package socialnetwork.observers;

import socialnetwork.models.Notification;

/**
 * Observer interface for notification events.
 * Implementations can deliver notifications through different channels.
 */
public interface NotificationObserver {
    
    /**
     * Called when a new notification is created.
     * 
     * @param notification The notification to deliver
     */
    void onNotification(Notification notification);
    
    /**
     * Get the name of this notification channel.
     */
    String getChannelName();
}



