package taskmanagement.observers;

import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.models.User;

/**
 * Observer interface for task lifecycle events.
 */
public interface TaskObserver {
    
    /**
     * Called when a new task is created.
     */
    void onTaskCreated(Task task, User creator);
    
    /**
     * Called when a task is updated.
     */
    void onTaskUpdated(Task task, String fieldName, String previousValue, String newValue, User updatedBy);
    
    /**
     * Called when a task is deleted.
     */
    void onTaskDeleted(Task task, User deletedBy);
    
    /**
     * Called when a task is assigned to a user.
     */
    void onTaskAssigned(Task task, User previousAssignee, User newAssignee, User assignedBy);
    
    /**
     * Called when a task status changes.
     */
    void onTaskStatusChanged(Task task, TaskStatus previousStatus, TaskStatus newStatus, User changedBy);
    
    /**
     * Called when a task is completed.
     */
    void onTaskCompleted(Task task, User completedBy);
}



