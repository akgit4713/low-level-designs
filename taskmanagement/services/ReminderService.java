package taskmanagement.services;

import taskmanagement.models.Reminder;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for reminder operations.
 */
public interface ReminderService {
    
    /**
     * Creates a reminder for a task.
     */
    Reminder createReminder(String taskId, String userId, LocalDateTime reminderTime, String message);
    
    /**
     * Gets a reminder by ID.
     */
    Optional<Reminder> getReminderById(String reminderId);
    
    /**
     * Gets all reminders for a task.
     */
    List<Reminder> getRemindersForTask(String taskId);
    
    /**
     * Gets all reminders for a user.
     */
    List<Reminder> getRemindersForUser(String userId);
    
    /**
     * Gets all pending (untriggered) reminders.
     */
    List<Reminder> getPendingReminders();
    
    /**
     * Gets upcoming reminders within specified minutes.
     */
    List<Reminder> getUpcomingReminders(int withinMinutes);
    
    /**
     * Updates a reminder's time.
     */
    Reminder updateReminderTime(String reminderId, LocalDateTime newTime);
    
    /**
     * Updates a reminder's message.
     */
    Reminder updateReminderMessage(String reminderId, String newMessage);
    
    /**
     * Deletes a reminder.
     */
    boolean deleteReminder(String reminderId);
    
    /**
     * Deletes all reminders for a task.
     */
    int deleteRemindersForTask(String taskId);
    
    /**
     * Checks and triggers any due reminders.
     * @return list of triggered reminders
     */
    List<Reminder> checkAndTriggerReminders();
}



