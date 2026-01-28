package employeedirectory.enums;

/**
 * Represents the department of an employee.
 */
public enum Department {
    ENGINEERING("Engineering"),
    PRODUCT("Product"),
    DESIGN("Design"),
    HR("Human Resources"),
    FINANCE("Finance"),
    MARKETING("Marketing"),
    SALES("Sales"),
    OPERATIONS("Operations");

    private final String displayName;

    Department(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
