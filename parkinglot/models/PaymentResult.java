package parkinglot.models;

import parkinglot.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents the result of a payment transaction.
 * Contains payment status, amount, and transaction details.
 */
public class PaymentResult {
    private final String transactionId;
    private final PaymentStatus status;
    private final double amount;
    private final LocalDateTime timestamp;
    private final String message;

    private PaymentResult(PaymentStatus status, double amount, String message) {
        this.transactionId = UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        this.status = status;
        this.amount = amount;
        this.timestamp = LocalDateTime.now();
        this.message = message;
    }

    public static PaymentResult success(double amount) {
        return new PaymentResult(PaymentStatus.COMPLETED, amount, "Payment successful");
    }

    public static PaymentResult failure(double amount, String reason) {
        return new PaymentResult(PaymentStatus.FAILED, amount, reason);
    }

    public static PaymentResult pending(double amount) {
        return new PaymentResult(PaymentStatus.PENDING, amount, "Payment pending");
    }

    public String getTransactionId() {
        return transactionId;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public double getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getMessage() {
        return message;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return String.format("PaymentResult[%s | %s | $%.2f | %s]",
            transactionId, status, amount, message);
    }
}



