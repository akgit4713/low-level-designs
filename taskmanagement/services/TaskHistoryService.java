package taskmanagement.services;

import taskmanagement.enums.HistoryAction;
import taskmanagement.models.TaskHistory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Service interface for task history operations.
 */
public interface TaskHistoryService {
    
    /**
     * Records a history entry.
     */
    TaskHistory recordHistory(String taskId, HistoryAction action, String fieldName,
                              String previousValue, String newValue, String changedBy);
    
    /**
     * Gets all history for a task.
     */
    List<TaskHistory> getHistoryForTask(String taskId);
    
    /**
     * Gets all history entries by a user.
     */
    List<TaskHistory> getHistoryByUser(String userId);
    
    /**
     * Gets history entries for a specific action.
     */
    List<TaskHistory> getHistoryByAction(HistoryAction action);
    
    /**
     * Gets recent history entries.
     */
    List<TaskHistory> getRecentHistory(int limit);
    
    /**
     * Gets history entries within a time range.
     */
    List<TaskHistory> getHistoryBetween(LocalDateTime start, LocalDateTime end);
    
    /**
     * Deletes all history for a task.
     */
    int deleteHistoryForTask(String taskId);
}



