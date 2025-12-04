package concertbooking.enums;

/**
 * Represents the status of a payment transaction
 */
public enum PaymentStatus {
    PENDING("Payment initiated"),
    PROCESSING("Payment being processed"),
    COMPLETED("Payment successful"),
    FAILED("Payment failed"),
    REFUNDED("Payment refunded");

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

    public boolean isFailed() {
        return this == FAILED;
    }
}



