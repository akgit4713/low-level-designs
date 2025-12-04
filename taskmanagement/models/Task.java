package taskmanagement.models;

import taskmanagement.enums.TaskPriority;
import taskmanagement.enums.TaskStatus;
import taskmanagement.exceptions.TaskException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents a task in the task management system.
 * Thread-safe for status updates.
 */
public class Task {
    private final String id;
    private String title;
    private String description;
    private LocalDateTime dueDate;
    private TaskPriority priority;
    private TaskStatus status;
    private final String createdBy;
    private String assignedTo;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime completedAt;
    
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();

    private Task(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.description = builder.description;
        this.dueDate = builder.dueDate;
        this.priority = builder.priority;
        this.status = builder.status;
        this.createdBy = builder.createdBy;
        this.assignedTo = builder.assignedTo;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = this.createdAt;
        this.completedAt = null;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        lock.readLock().lock();
        try {
            return title;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getDescription() {
        lock.readLock().lock();
        try {
            return description;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LocalDateTime getDueDate() {
        lock.readLock().lock();
        try {
            return dueDate;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TaskPriority getPriority() {
        lock.readLock().lock();
        try {
            return priority;
        } finally {
            lock.readLock().unlock();
        }
    }

    public TaskStatus getStatus() {
        lock.readLock().lock();
        try {
            return status;
        } finally {
            lock.readLock().unlock();
        }
    }

    public String getCreatedBy() {
        return createdBy;
    }

    public String getAssignedTo() {
        lock.readLock().lock();
        try {
            return assignedTo;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        lock.readLock().lock();
        try {
            return updatedAt;
        } finally {
            lock.readLock().unlock();
        }
    }

    public LocalDateTime getCompletedAt() {
        lock.readLock().lock();
        try {
            return completedAt;
        } finally {
            lock.readLock().unlock();
        }
    }

    // Mutators
    public void setTitle(String title) {
        lock.writeLock().lock();
        try {
            this.title = Objects.requireNonNull(title, "Title cannot be null");
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setDescription(String description) {
        lock.writeLock().lock();
        try {
            this.description = description;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setDueDate(LocalDateTime dueDate) {
        lock.writeLock().lock();
        try {
            this.dueDate = dueDate;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void setPriority(TaskPriority priority) {
        lock.writeLock().lock();
        try {
            this.priority = Objects.requireNonNull(priority, "Priority cannot be null");
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Updates the task status with validation.
     * @throws TaskException if the transition is not valid
     */
    public void updateStatus(TaskStatus newStatus) {
        lock.writeLock().lock();
        try {
            if (!this.status.canTransitionTo(newStatus)) {
                throw new TaskException("Invalid status transition from " + 
                    this.status + " to " + newStatus);
            }
            this.status = newStatus;
            this.updatedAt = LocalDateTime.now();
            
            if (newStatus == TaskStatus.COMPLETED) {
                this.completedAt = LocalDateTime.now();
            }
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Assigns this task to a user.
     */
    public void assignTo(String userId) {
        lock.writeLock().lock();
        try {
            this.assignedTo = userId;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Unassigns this task from any user.
     */
    public void unassign() {
        lock.writeLock().lock();
        try {
            this.assignedTo = null;
            this.updatedAt = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Checks if this task is overdue.
     */
    public boolean isOverdue() {
        lock.readLock().lock();
        try {
            return dueDate != null && 
                   LocalDateTime.now().isAfter(dueDate) && 
                   status.isActive();
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Checks if this task is assigned to anyone.
     */
    public boolean isAssigned() {
        lock.readLock().lock();
        try {
            return assignedTo != null;
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return Objects.equals(id, task.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        lock.readLock().lock();
        try {
            return "Task{" +
                    "id='" + id + '\'' +
                    ", title='" + title + '\'' +
                    ", priority=" + priority +
                    ", status=" + status +
                    ", assignedTo='" + assignedTo + '\'' +
                    ", dueDate=" + dueDate +
                    '}';
        } finally {
            lock.readLock().unlock();
        }
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String title;
        private String description;
        private LocalDateTime dueDate;
        private TaskPriority priority = TaskPriority.MEDIUM;
        private TaskStatus status = TaskStatus.PENDING;
        private String createdBy;
        private String assignedTo;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder dueDate(LocalDateTime dueDate) {
            this.dueDate = dueDate;
            return this;
        }

        public Builder priority(TaskPriority priority) {
            this.priority = priority;
            return this;
        }

        public Builder status(TaskStatus status) {
            this.status = status;
            return this;
        }

        public Builder createdBy(String createdBy) {
            this.createdBy = createdBy;
            return this;
        }

        public Builder assignedTo(String assignedTo) {
            this.assignedTo = assignedTo;
            return this;
        }

        public Task build() {
            validate();
            return new Task(this);
        }

        private void validate() {
            if (id == null || id.isBlank()) {
                throw new IllegalArgumentException("Task ID is required");
            }
            if (title == null || title.isBlank()) {
                throw new IllegalArgumentException("Task title is required");
            }
            if (createdBy == null || createdBy.isBlank()) {
                throw new IllegalArgumentException("CreatedBy user ID is required");
            }
        }
    }
}



