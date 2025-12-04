package atm.enums;

/**
 * Types of transactions supported by the ATM.
 */
public enum TransactionType {
    BALANCE_INQUIRY("Balance Inquiry"),
    WITHDRAWAL("Cash Withdrawal"),
    DEPOSIT("Cash Deposit"),
    PIN_CHANGE("PIN Change"),
    MINI_STATEMENT("Mini Statement");

    private final String displayName;

    TransactionType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



