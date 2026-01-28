package employeedirectory.enums;

/**
 * Represents the employment status of an employee.
 */
public enum EmployeeStatus {
    ACTIVE("Active"),
    ON_LEAVE("On Leave"),
    TERMINATED("Terminated"),
    RESIGNED("Resigned"),
    PROBATION("Probation");

    private final String displayName;

    EmployeeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
