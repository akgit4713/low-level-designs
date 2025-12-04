package taskmanagement.enums;

/**
 * Represents the type of action recorded in task history.
 */
public enum HistoryAction {
    CREATED("Task Created"),
    UPDATED("Task Updated"),
    DELETED("Task Deleted"),
    ASSIGNED("Task Assigned"),
    UNASSIGNED("Task Unassigned"),
    STATUS_CHANGED("Status Changed"),
    PRIORITY_CHANGED("Priority Changed"),
    DUE_DATE_CHANGED("Due Date Changed"),
    COMPLETED("Task Completed"),
    CANCELLED("Task Cancelled"),
    REMINDER_SET("Reminder Set"),
    REMINDER_TRIGGERED("Reminder Triggered");

    private final String displayName;

    HistoryAction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



