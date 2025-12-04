package digitalwallet.enums;

/**
 * Status of a transaction throughout its lifecycle.
 * Includes valid state transitions to ensure consistency.
 */
public enum TransactionStatus {
    PENDING("Pending"),
    PROCESSING("Processing"),
    COMPLETED("Completed"),
    FAILED("Failed"),
    CANCELLED("Cancelled"),
    REVERSED("Reversed");

    private final String displayName;

    TransactionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if transition to new status is valid
     */
    public boolean canTransitionTo(TransactionStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == PROCESSING || newStatus == CANCELLED || newStatus == FAILED;
            case PROCESSING -> newStatus == COMPLETED || newStatus == FAILED;
            case COMPLETED -> newStatus == REVERSED;
            case FAILED, CANCELLED, REVERSED -> false; // Terminal states
        };
    }

    /**
     * Returns true if this is a terminal state (no further transitions allowed)
     */
    public boolean isTerminal() {
        return this == FAILED || this == CANCELLED || this == REVERSED;
    }

    /**
     * Returns true if this status indicates a successful transaction
     */
    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}



