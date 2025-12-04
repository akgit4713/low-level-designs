package taskmanagement.exceptions;

/**
 * Exception for reminder-related errors.
 */
public class ReminderException extends RuntimeException {
    
    public ReminderException(String message) {
        super(message);
    }
    
    public ReminderException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ReminderException notFound(String reminderId) {
        return new ReminderException("Reminder not found with ID: " + reminderId);
    }
    
    public static ReminderException alreadyTriggered(String reminderId) {
        return new ReminderException("Reminder already triggered: " + reminderId);
    }
    
    public static ReminderException pastTime() {
        return new ReminderException("Reminder time cannot be in the past");
    }
    
    public static ReminderException invalidTask(String taskId) {
        return new ReminderException("Cannot create reminder for invalid task: " + taskId);
    }
}



