package digitalwallet.enums;

/**
 * Types of payment methods supported by the digital wallet.
 */
public enum PaymentMethodType {
    CREDIT_CARD("Credit Card", true, true),
    DEBIT_CARD("Debit Card", true, true),
    BANK_ACCOUNT("Bank Account", true, true),
    WALLET_BALANCE("Wallet Balance", false, false);

    private final String displayName;
    private final boolean supportsDeposit;
    private final boolean supportsWithdrawal;

    PaymentMethodType(String displayName, boolean supportsDeposit, boolean supportsWithdrawal) {
        this.displayName = displayName;
        this.supportsDeposit = supportsDeposit;
        this.supportsWithdrawal = supportsWithdrawal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean supportsDeposit() {
        return supportsDeposit;
    }

    public boolean supportsWithdrawal() {
        return supportsWithdrawal;
    }
}



