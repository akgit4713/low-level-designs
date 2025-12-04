package taskmanagement.strategies.notification;

import taskmanagement.models.Reminder;
import taskmanagement.models.Task;
import taskmanagement.models.User;

/**
 * Strategy interface for sending notifications.
 */
public interface NotificationStrategy {
    
    /**
     * Sends a notification to the user.
     * @param user the user to notify
     * @param reminder the reminder that triggered this notification
     * @param task the task associated with the reminder
     */
    void notify(User user, Reminder reminder, Task task);
    
    /**
     * Sends a general notification to the user.
     * @param user the user to notify
     * @param subject the notification subject
     * @param message the notification message
     */
    void notify(User user, String subject, String message);
    
    /**
     * Returns the type of this notification strategy.
     */
    String getType();
}



