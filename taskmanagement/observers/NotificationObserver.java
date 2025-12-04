package taskmanagement.observers;

import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.models.User;
import taskmanagement.strategies.notification.NotificationStrategy;

/**
 * Observer that sends notifications for task events.
 */
public class NotificationObserver implements TaskObserver {
    
    private final NotificationStrategy notificationStrategy;
    private final boolean notifyOnAssignment;
    private final boolean notifyOnCompletion;
    private final boolean notifyOnStatusChange;

    public NotificationObserver(NotificationStrategy notificationStrategy) {
        this(notificationStrategy, true, true, false);
    }

    public NotificationObserver(NotificationStrategy notificationStrategy,
                                boolean notifyOnAssignment,
                                boolean notifyOnCompletion,
                                boolean notifyOnStatusChange) {
        this.notificationStrategy = notificationStrategy;
        this.notifyOnAssignment = notifyOnAssignment;
        this.notifyOnCompletion = notifyOnCompletion;
        this.notifyOnStatusChange = notifyOnStatusChange;
    }

    @Override
    public void onTaskCreated(Task task, User creator) {
        // No notification on task creation by default
    }

    @Override
    public void onTaskUpdated(Task task, String fieldName, String previousValue, String newValue, User updatedBy) {
        // No notification on general updates by default
    }

    @Override
    public void onTaskDeleted(Task task, User deletedBy) {
        // No notification on task deletion by default
    }

    @Override
    public void onTaskAssigned(Task task, User previousAssignee, User newAssignee, User assignedBy) {
        if (!notifyOnAssignment || newAssignee == null) {
            return;
        }
        
        String subject = "New Task Assigned: " + task.getTitle();
        String message = buildAssignmentMessage(task, previousAssignee, assignedBy);
        notificationStrategy.notify(newAssignee, subject, message);
    }

    @Override
    public void onTaskStatusChanged(Task task, TaskStatus previousStatus, TaskStatus newStatus, User changedBy) {
        if (!notifyOnStatusChange) {
            return;
        }
        
        // Notify the task creator about status changes
        // In a real implementation, we would look up the creator User object
        String subject = "Task Status Updated: " + task.getTitle();
        String message = String.format(
                "Task '%s' status changed from %s to %s.",
                task.getTitle(),
                previousStatus.getDisplayName(),
                newStatus.getDisplayName()
        );
        
        // We would notify task creator here if we had the user service
        System.out.println("[NOTIFICATION] Status change: " + message);
    }

    @Override
    public void onTaskCompleted(Task task, User completedBy) {
        if (!notifyOnCompletion) {
            return;
        }
        
        // Notify about task completion
        // In a real implementation, notify the task creator
        String subject = "Task Completed: " + task.getTitle();
        String message = String.format(
                "Task '%s' has been marked as completed by %s.",
                task.getTitle(),
                completedBy != null ? completedBy.getName() : "Unknown"
        );
        
        System.out.println("[NOTIFICATION] Task completed: " + message);
    }
    
    private String buildAssignmentMessage(Task task, User previousAssignee, User assignedBy) {
        StringBuilder sb = new StringBuilder();
        sb.append("You have been assigned a new task:\n\n");
        sb.append("Task: ").append(task.getTitle()).append("\n");
        sb.append("Priority: ").append(task.getPriority().getDisplayName()).append("\n");
        sb.append("Status: ").append(task.getStatus().getDisplayName()).append("\n");
        
        if (task.getDueDate() != null) {
            sb.append("Due Date: ").append(task.getDueDate()).append("\n");
        }
        
        if (task.getDescription() != null && !task.getDescription().isEmpty()) {
            sb.append("\nDescription: ").append(task.getDescription()).append("\n");
        }
        
        if (assignedBy != null) {
            sb.append("\nAssigned by: ").append(assignedBy.getName());
        }
        
        if (previousAssignee != null) {
            sb.append("\nPreviously assigned to: ").append(previousAssignee.getName());
        }
        
        return sb.toString();
    }
    
    public NotificationStrategy getNotificationStrategy() {
        return notificationStrategy;
    }
}



