package carrental.enums;

/**
 * Represents the status of a payment.
 */
public enum PaymentStatus {
    PENDING("Payment pending"),
    PROCESSING("Payment being processed"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded"),
    PARTIALLY_REFUNDED("Payment partially refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



