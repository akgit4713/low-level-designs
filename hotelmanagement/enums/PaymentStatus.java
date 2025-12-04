package hotelmanagement.enums;

/**
 * Enum representing the status of a payment transaction
 */
public enum PaymentStatus {
    PENDING("Payment is pending"),
    COMPLETED("Payment completed successfully"),
    FAILED("Payment failed"),
    REFUNDED("Payment was refunded"),
    PARTIALLY_REFUNDED("Payment was partially refunded");

    private final String description;

    PaymentStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isSuccessful() {
        return this == COMPLETED;
    }
}



