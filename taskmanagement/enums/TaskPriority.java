package taskmanagement.enums;

/**
 * Represents the priority level of a task.
 */
public enum TaskPriority {
    LOW(1, "Low"),
    MEDIUM(2, "Medium"),
    HIGH(3, "High"),
    CRITICAL(4, "Critical");

    private final int level;
    private final String displayName;

    TaskPriority(int level, String displayName) {
        this.level = level;
        this.displayName = displayName;
    }

    public int getLevel() {
        return level;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Creates a TaskPriority from its numeric level.
     */
    public static TaskPriority fromLevel(int level) {
        for (TaskPriority priority : values()) {
            if (priority.level == level) {
                return priority;
            }
        }
        throw new IllegalArgumentException("Invalid priority level: " + level);
    }

    /**
     * Checks if this priority is higher than the given priority.
     */
    public boolean isHigherThan(TaskPriority other) {
        return this.level > other.level;
    }

    /**
     * Checks if this priority is at least as high as the given priority.
     */
    public boolean isAtLeast(TaskPriority other) {
        return this.level >= other.level;
    }
}



