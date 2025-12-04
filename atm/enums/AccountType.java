package atm.enums;

/**
 * Types of bank accounts.
 */
public enum AccountType {
    SAVINGS("Savings Account"),
    CHECKING("Checking Account"),
    CURRENT("Current Account");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



