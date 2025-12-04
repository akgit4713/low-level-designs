package bookmyshow.enums;

/**
 * Status of a payment transaction.
 */
public enum PaymentStatus {
    PENDING,        // Payment initiated
    PROCESSING,     // Payment being processed
    COMPLETED,      // Payment successful
    FAILED,         // Payment failed
    REFUNDED,       // Payment refunded
    CANCELLED       // Payment cancelled
}



