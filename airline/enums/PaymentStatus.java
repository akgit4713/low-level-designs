package airline.enums;

/**
 * Status of a payment transaction.
 */
public enum PaymentStatus {
    PENDING("Payment is pending"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    REFUNDED("Payment has been refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



