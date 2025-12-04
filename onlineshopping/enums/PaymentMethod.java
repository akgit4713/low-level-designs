package onlineshopping.enums;

/**
 * Supported payment methods
 */
public enum PaymentMethod {
    CREDIT_CARD("Credit Card", true),
    DEBIT_CARD("Debit Card", true),
    UPI("UPI", true),
    WALLET("Wallet", true),
    COD("Cash on Delivery", false);

    private final String displayName;
    private final boolean prepaid;

    PaymentMethod(String displayName, boolean prepaid) {
        this.displayName = displayName;
        this.prepaid = prepaid;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Check if payment is collected before delivery
     */
    public boolean isPrepaid() {
        return prepaid;
    }
}



