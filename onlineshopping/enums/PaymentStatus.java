package onlineshopping.enums;

/**
 * Payment transaction status
 */
public enum PaymentStatus {
    PENDING,
    PROCESSING,
    COMPLETED,
    FAILED,
    REFUNDED,
    PARTIALLY_REFUNDED;

    public boolean isSuccessful() {
        return this == COMPLETED;
    }

    public boolean isFinal() {
        return this == COMPLETED || this == FAILED || this == REFUNDED;
    }
}



