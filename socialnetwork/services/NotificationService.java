package socialnetwork.services;

import socialnetwork.models.Notification;
import socialnetwork.observers.NotificationObserver;

import java.util.List;

/**
 * Service interface for notification operations.
 */
public interface NotificationService {
    
    /**
     * Send a notification to all registered observers.
     */
    void sendNotification(Notification notification);
    
    /**
     * Get all notifications for a user.
     */
    List<Notification> getNotifications(String userId);
    
    /**
     * Get unread notifications for a user.
     */
    List<Notification> getUnreadNotifications(String userId);
    
    /**
     * Get unread notification count.
     */
    int getUnreadCount(String userId);
    
    /**
     * Mark a notification as read.
     */
    void markAsRead(String notificationId);
    
    /**
     * Mark all notifications as read for a user.
     */
    void markAllAsRead(String userId);
    
    /**
     * Register a notification observer.
     */
    void registerObserver(NotificationObserver observer);
    
    /**
     * Unregister a notification observer.
     */
    void unregisterObserver(NotificationObserver observer);
}



