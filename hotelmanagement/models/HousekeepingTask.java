package hotelmanagement.models;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a housekeeping or maintenance task for a room
 */
public class HousekeepingTask {
    
    public enum TaskType {
        CLEANING("Room Cleaning"),
        DEEP_CLEANING("Deep Cleaning"),
        TURNDOWN("Turndown Service"),
        MAINTENANCE("Maintenance"),
        INSPECTION("Room Inspection");

        private final String displayName;

        TaskType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum TaskStatus {
        PENDING,
        IN_PROGRESS,
        COMPLETED,
        CANCELLED
    }

    private final String id;
    private final Room room;
    private final TaskType taskType;
    private final LocalDateTime createdAt;
    private final String priority;  // HIGH, MEDIUM, LOW
    
    private volatile TaskStatus status;
    private String assignedTo;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String notes;

    public HousekeepingTask(Room room, TaskType taskType, String priority) {
        this.id = "TASK-" + UUID.randomUUID().toString().substring(0, 8);
        this.room = room;
        this.taskType = taskType;
        this.priority = priority;
        this.status = TaskStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public Room getRoom() {
        return room;
    }

    public TaskType getTaskType() {
        return taskType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public String getPriority() {
        return priority;
    }

    public TaskStatus getStatus() {
        return status;
    }

    public String getAssignedTo() {
        return assignedTo;
    }

    public void assignTo(String staffName) {
        this.assignedTo = staffName;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Start working on this task
     */
    public void start() {
        if (status == TaskStatus.PENDING) {
            this.status = TaskStatus.IN_PROGRESS;
            this.startedAt = LocalDateTime.now();
        }
    }

    /**
     * Mark task as completed
     */
    public void complete() {
        if (status == TaskStatus.IN_PROGRESS) {
            this.status = TaskStatus.COMPLETED;
            this.completedAt = LocalDateTime.now();
        }
    }

    /**
     * Cancel the task
     */
    public void cancel() {
        if (status != TaskStatus.COMPLETED) {
            this.status = TaskStatus.CANCELLED;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HousekeepingTask that = (HousekeepingTask) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("HousekeepingTask{id='%s', room='%s', type=%s, status=%s, priority=%s}",
            id, room.getRoomNumber(), taskType, status, priority);
    }
}



