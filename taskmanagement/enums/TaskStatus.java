package taskmanagement.enums;

import java.util.Set;
import java.util.EnumSet;

/**
 * Represents the status of a task with valid state transitions.
 */
public enum TaskStatus {
    PENDING("Pending"),
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    CANCELLED("Cancelled");

    private final String displayName;

    TaskStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Checks if the current status can transition to the given status.
     */
    public boolean canTransitionTo(TaskStatus newStatus) {
        return getValidTransitions().contains(newStatus);
    }

    /**
     * Returns the set of valid status transitions from the current status.
     */
    public Set<TaskStatus> getValidTransitions() {
        return switch (this) {
            case PENDING -> EnumSet.of(IN_PROGRESS, CANCELLED);
            case IN_PROGRESS -> EnumSet.of(COMPLETED, PENDING, CANCELLED);
            case COMPLETED -> EnumSet.noneOf(TaskStatus.class); // Terminal state
            case CANCELLED -> EnumSet.noneOf(TaskStatus.class); // Terminal state
        };
    }

    /**
     * Checks if this status is a terminal state (no further transitions possible).
     */
    public boolean isTerminal() {
        return this == COMPLETED || this == CANCELLED;
    }

    /**
     * Checks if this status represents an active task.
     */
    public boolean isActive() {
        return this == PENDING || this == IN_PROGRESS;
    }
}



