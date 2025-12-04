package cricinfo.enums;

/**
 * Represents the status of an innings.
 */
public enum InningsStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    ALL_OUT("All Out"),
    DECLARED("Declared"),
    TARGET_ACHIEVED("Target Achieved"),
    COMPLETED("Completed");

    private final String displayName;

    InningsStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



