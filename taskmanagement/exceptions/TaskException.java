package taskmanagement.exceptions;

/**
 * Exception for task-related errors.
 */
public class TaskException extends RuntimeException {
    
    public TaskException(String message) {
        super(message);
    }
    
    public TaskException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static TaskException notFound(String taskId) {
        return new TaskException("Task not found with ID: " + taskId);
    }
    
    public static TaskException invalidStatusTransition(String from, String to) {
        return new TaskException("Invalid status transition from " + from + " to " + to);
    }
    
    public static TaskException alreadyCompleted(String taskId) {
        return new TaskException("Task is already completed: " + taskId);
    }
    
    public static TaskException alreadyCancelled(String taskId) {
        return new TaskException("Task is already cancelled: " + taskId);
    }
}



