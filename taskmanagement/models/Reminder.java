package taskmanagement.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a reminder for a task.
 */
public class Reminder {
    private final String id;
    private final String taskId;
    private final String userId;
    private LocalDateTime reminderTime;
    private String message;
    private boolean triggered;
    private final LocalDateTime createdAt;
    private LocalDateTime triggeredAt;

    public Reminder(String id, String taskId, String userId, LocalDateTime reminderTime, String message) {
        this.id = Objects.requireNonNull(id, "Reminder ID cannot be null");
        this.taskId = Objects.requireNonNull(taskId, "Task ID cannot be null");
        this.userId = Objects.requireNonNull(userId, "User ID cannot be null");
        this.reminderTime = Objects.requireNonNull(reminderTime, "Reminder time cannot be null");
        this.message = message != null ? message : "Task reminder";
        this.triggered = false;
        this.createdAt = LocalDateTime.now();
        this.triggeredAt = null;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getReminderTime() {
        return reminderTime;
    }

    public String getMessage() {
        return message;
    }

    public boolean isTriggered() {
        return triggered;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getTriggeredAt() {
        return triggeredAt;
    }

    // Setters
    public void setReminderTime(LocalDateTime reminderTime) {
        this.reminderTime = Objects.requireNonNull(reminderTime, "Reminder time cannot be null");
    }

    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Marks this reminder as triggered.
     */
    public void markTriggered() {
        this.triggered = true;
        this.triggeredAt = LocalDateTime.now();
    }

    /**
     * Checks if this reminder is due (time has passed and not yet triggered).
     */
    public boolean isDue() {
        return !triggered && LocalDateTime.now().isAfter(reminderTime);
    }

    /**
     * Checks if this reminder is upcoming within the specified minutes.
     */
    public boolean isUpcomingWithin(int minutes) {
        if (triggered) return false;
        LocalDateTime now = LocalDateTime.now();
        return reminderTime.isAfter(now) && 
               reminderTime.isBefore(now.plusMinutes(minutes));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reminder reminder = (Reminder) o;
        return Objects.equals(id, reminder.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Reminder{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", userId='" + userId + '\'' +
                ", reminderTime=" + reminderTime +
                ", message='" + message + '\'' +
                ", triggered=" + triggered +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String taskId;
        private String userId;
        private LocalDateTime reminderTime;
        private String message;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder userId(String userId) {
            this.userId = userId;
            return this;
        }

        public Builder reminderTime(LocalDateTime reminderTime) {
            this.reminderTime = reminderTime;
            return this;
        }

        public Builder message(String message) {
            this.message = message;
            return this;
        }

        public Reminder build() {
            return new Reminder(id, taskId, userId, reminderTime, message);
        }
    }
}



