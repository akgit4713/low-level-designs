package hotelmanagement.services;

import hotelmanagement.models.HousekeepingTask;
import hotelmanagement.models.Room;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for housekeeping operations
 */
public interface HousekeepingService {
    
    /**
     * Create a cleaning task for a room
     */
    HousekeepingTask createCleaningTask(Room room, String priority);
    
    /**
     * Create a maintenance task for a room
     */
    HousekeepingTask createMaintenanceTask(Room room, String issue, String priority);
    
    /**
     * Get a task by ID
     */
    Optional<HousekeepingTask> getTask(String taskId);
    
    /**
     * Get all pending tasks
     */
    List<HousekeepingTask> getPendingTasks();
    
    /**
     * Get pending tasks by priority
     */
    List<HousekeepingTask> getPendingTasksByPriority(String priority);
    
    /**
     * Assign task to staff member
     */
    void assignTask(String taskId, String staffName);
    
    /**
     * Start working on a task
     */
    void startTask(String taskId);
    
    /**
     * Complete a task
     */
    void completeTask(String taskId);
    
    /**
     * Cancel a task
     */
    void cancelTask(String taskId);
    
    /**
     * Get tasks for a specific room
     */
    List<HousekeepingTask> getTasksForRoom(String roomId);
    
    /**
     * Mark room as cleaned and available
     */
    void markRoomClean(String roomId);
}



