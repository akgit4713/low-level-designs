package hotelmanagement.enums;

/**
 * Enum representing available payment methods
 */
public enum PaymentMethod {
    CASH("Cash Payment"),
    CREDIT_CARD("Credit Card"),
    DEBIT_CARD("Debit Card"),
    ONLINE("Online Payment (UPI/Net Banking)");

    private final String displayName;

    PaymentMethod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



