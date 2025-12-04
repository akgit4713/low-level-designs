package taskmanagement.services;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.models.Task;
import taskmanagement.strategies.search.SearchCriteria;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for searching and filtering tasks.
 */
public interface SearchService {
    
    /**
     * Searches tasks using the given criteria.
     */
    List<Task> search(SearchCriteria criteria);
    
    /**
     * Searches tasks by priority.
     */
    List<Task> searchByPriority(TaskPriority priority);
    
    /**
     * Searches tasks with at least the specified priority.
     */
    List<Task> searchByPriorityAtLeast(TaskPriority minPriority);
    
    /**
     * Searches tasks by status.
     */
    List<Task> searchByStatus(TaskStatus status);
    
    /**
     * Searches tasks by multiple statuses.
     */
    List<Task> searchByStatuses(TaskStatus... statuses);
    
    /**
     * Searches tasks assigned to a user.
     */
    List<Task> searchByAssignee(String userId);
    
    /**
     * Searches unassigned tasks.
     */
    List<Task> searchUnassigned();
    
    /**
     * Searches tasks due before a date.
     */
    List<Task> searchByDueDateBefore(LocalDateTime date);
    
    /**
     * Searches tasks due between two dates.
     */
    List<Task> searchByDueDateBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Searches overdue tasks.
     */
    List<Task> searchOverdue();
    
    /**
     * Searches tasks due today.
     */
    List<Task> searchDueToday();
    
    /**
     * Searches tasks by title (partial match).
     */
    List<Task> searchByTitle(String titleQuery);
    
    /**
     * Searches active tasks (pending or in progress).
     */
    List<Task> searchActiveTasks();
}



