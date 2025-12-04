package taskmanagement.repositories;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Task entities with additional query methods.
 */
public interface TaskRepository extends Repository<Task, String> {
    
    /**
     * Finds all tasks created by a user.
     */
    List<Task> findByCreatedBy(String userId);
    
    /**
     * Finds all tasks assigned to a user.
     */
    List<Task> findByAssignedTo(String userId);
    
    /**
     * Finds all tasks with a specific status.
     */
    List<Task> findByStatus(TaskStatus status);
    
    /**
     * Finds all tasks with a specific priority.
     */
    List<Task> findByPriority(TaskPriority priority);
    
    /**
     * Finds all tasks due before a specific date.
     */
    List<Task> findByDueDateBefore(LocalDateTime date);
    
    /**
     * Finds all tasks due between two dates.
     */
    List<Task> findByDueDateBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Finds all overdue tasks.
     */
    List<Task> findOverdueTasks();
    
    /**
     * Finds all active tasks (not completed or cancelled).
     */
    List<Task> findActiveTasks();
}



