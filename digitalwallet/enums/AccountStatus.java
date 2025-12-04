package digitalwallet.enums;

/**
 * Status of a user account in the digital wallet system.
 */
public enum AccountStatus {
    PENDING_VERIFICATION("Pending Verification", false, false),
    ACTIVE("Active", true, true),
    SUSPENDED("Suspended", true, false),
    CLOSED("Closed", false, false);

    private final String displayName;
    private final boolean canViewBalance;
    private final boolean canTransact;

    AccountStatus(String displayName, boolean canViewBalance, boolean canTransact) {
        this.displayName = displayName;
        this.canViewBalance = canViewBalance;
        this.canTransact = canTransact;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean canViewBalance() {
        return canViewBalance;
    }

    public boolean canTransact() {
        return canTransact;
    }

    /**
     * Check if account can be reactivated from this status
     */
    public boolean canReactivate() {
        return this == SUSPENDED;
    }
}



