package taskmanagement.services;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.observers.TaskObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for task operations.
 */
public interface TaskService {
    
    /**
     * Creates a new task.
     */
    Task createTask(String title, String description, TaskPriority priority, 
                    LocalDateTime dueDate, String createdBy);
    
    /**
     * Gets a task by ID.
     */
    Optional<Task> getTaskById(String taskId);
    
    /**
     * Gets all tasks.
     */
    List<Task> getAllTasks();
    
    /**
     * Gets tasks created by a user.
     */
    List<Task> getTasksCreatedBy(String userId);
    
    /**
     * Gets tasks assigned to a user.
     */
    List<Task> getTasksAssignedTo(String userId);
    
    /**
     * Updates a task's title.
     */
    Task updateTitle(String taskId, String newTitle, String updatedBy);
    
    /**
     * Updates a task's description.
     */
    Task updateDescription(String taskId, String newDescription, String updatedBy);
    
    /**
     * Updates a task's priority.
     */
    Task updatePriority(String taskId, TaskPriority newPriority, String updatedBy);
    
    /**
     * Updates a task's due date.
     */
    Task updateDueDate(String taskId, LocalDateTime newDueDate, String updatedBy);
    
    /**
     * Updates a task's status.
     */
    Task updateStatus(String taskId, TaskStatus newStatus, String updatedBy);
    
    /**
     * Assigns a task to a user.
     */
    Task assignTask(String taskId, String assigneeId, String assignedBy);
    
    /**
     * Unassigns a task.
     */
    Task unassignTask(String taskId, String unassignedBy);
    
    /**
     * Marks a task as completed.
     */
    Task completeTask(String taskId, String completedBy);
    
    /**
     * Cancels a task.
     */
    Task cancelTask(String taskId, String cancelledBy);
    
    /**
     * Deletes a task.
     */
    boolean deleteTask(String taskId, String deletedBy);
    
    /**
     * Registers an observer for task events.
     */
    void registerObserver(TaskObserver observer);
    
    /**
     * Unregisters an observer.
     */
    void unregisterObserver(TaskObserver observer);
}



