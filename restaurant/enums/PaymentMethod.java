package restaurant.enums;

/**
 * Supported payment methods
 */
public enum PaymentMethod {
    CASH("Cash Payment"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    MOBILE_PAYMENT("Mobile Payment (UPI/Apple Pay/Google Pay)"),
    GIFT_CARD("Gift Card");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}

