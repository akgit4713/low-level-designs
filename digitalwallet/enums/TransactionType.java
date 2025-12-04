package digitalwallet.enums;

/**
 * Types of transactions in the digital wallet system.
 */
public enum TransactionType {
    DEPOSIT("Deposit", true),
    WITHDRAWAL("Withdrawal", false),
    TRANSFER_IN("Transfer Received", true),
    TRANSFER_OUT("Transfer Sent", false),
    CURRENCY_EXCHANGE_DEBIT("Currency Exchange (From)", false),
    CURRENCY_EXCHANGE_CREDIT("Currency Exchange (To)", true),
    FEE("Transaction Fee", false),
    REFUND("Refund", true),
    REVERSAL("Reversal", true);

    private final String displayName;
    private final boolean credit;

    TransactionType(String displayName, boolean credit) {
        this.displayName = displayName;
        this.credit = credit;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Returns true if this transaction type adds money to the wallet
     */
    public boolean isCredit() {
        return credit;
    }

    /**
     * Returns true if this transaction type removes money from the wallet
     */
    public boolean isDebit() {
        return !credit;
    }
}



