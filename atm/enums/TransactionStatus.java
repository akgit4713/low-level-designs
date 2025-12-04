package atm.enums;

/**
 * Status of a transaction.
 */
public enum TransactionStatus {
    PENDING("Transaction in progress"),
    SUCCESS("Transaction successful"),
    FAILED("Transaction failed"),
    CANCELLED("Transaction cancelled by user"),
    INSUFFICIENT_FUNDS("Insufficient funds"),
    LIMIT_EXCEEDED("Daily limit exceeded"),
    INSUFFICIENT_CASH("ATM has insufficient cash");

    private final String message;

    TransactionStatus(String message) {
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return this == SUCCESS;
    }

    public boolean isFailed() {
        return this == FAILED || this == INSUFFICIENT_FUNDS || 
               this == LIMIT_EXCEEDED || this == INSUFFICIENT_CASH;
    }
}



