package taskmanagement.models;

import taskmanagement.enums.HistoryAction;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a history record for task changes.
 */
public class TaskHistory {
    private final String id;
    private final String taskId;
    private final HistoryAction action;
    private final String fieldName;
    private final String previousValue;
    private final String newValue;
    private final String changedBy;
    private final LocalDateTime timestamp;

    public TaskHistory(String id, String taskId, HistoryAction action, 
                       String fieldName, String previousValue, String newValue, 
                       String changedBy) {
        this.id = Objects.requireNonNull(id, "History ID cannot be null");
        this.taskId = Objects.requireNonNull(taskId, "Task ID cannot be null");
        this.action = Objects.requireNonNull(action, "Action cannot be null");
        this.fieldName = fieldName;
        this.previousValue = previousValue;
        this.newValue = newValue;
        this.changedBy = changedBy;
        this.timestamp = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTaskId() {
        return taskId;
    }

    public HistoryAction getAction() {
        return action;
    }

    public String getFieldName() {
        return fieldName;
    }

    public String getPreviousValue() {
        return previousValue;
    }

    public String getNewValue() {
        return newValue;
    }

    public String getChangedBy() {
        return changedBy;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    /**
     * Returns a human-readable description of this history entry.
     */
    public String getDescription() {
        StringBuilder sb = new StringBuilder();
        sb.append(action.getDisplayName());
        
        if (fieldName != null && !fieldName.isEmpty()) {
            sb.append(" - ").append(fieldName);
        }
        
        if (previousValue != null && newValue != null) {
            sb.append(": ").append(previousValue).append(" -> ").append(newValue);
        } else if (newValue != null) {
            sb.append(": ").append(newValue);
        }
        
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TaskHistory that = (TaskHistory) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "TaskHistory{" +
                "id='" + id + '\'' +
                ", taskId='" + taskId + '\'' +
                ", action=" + action +
                ", fieldName='" + fieldName + '\'' +
                ", previousValue='" + previousValue + '\'' +
                ", newValue='" + newValue + '\'' +
                ", changedBy='" + changedBy + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String taskId;
        private HistoryAction action;
        private String fieldName;
        private String previousValue;
        private String newValue;
        private String changedBy;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder taskId(String taskId) {
            this.taskId = taskId;
            return this;
        }

        public Builder action(HistoryAction action) {
            this.action = action;
            return this;
        }

        public Builder fieldName(String fieldName) {
            this.fieldName = fieldName;
            return this;
        }

        public Builder previousValue(String previousValue) {
            this.previousValue = previousValue;
            return this;
        }

        public Builder newValue(String newValue) {
            this.newValue = newValue;
            return this;
        }

        public Builder changedBy(String changedBy) {
            this.changedBy = changedBy;
            return this;
        }

        public TaskHistory build() {
            return new TaskHistory(id, taskId, action, fieldName, 
                                   previousValue, newValue, changedBy);
        }
    }
}



