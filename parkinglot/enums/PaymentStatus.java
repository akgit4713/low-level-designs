package parkinglot.enums;

/**
 * Enumeration of payment transaction states.
 */
public enum PaymentStatus {
    PENDING("Payment not yet processed"),
    COMPLETED("Payment successfully completed"),
    FAILED("Payment failed");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



