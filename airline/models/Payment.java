package airline.models;

import airline.enums.PaymentMethod;
import airline.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a payment transaction.
 */
public class Payment {
    private final String id;
    private final String bookingId;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private volatile PaymentStatus status;
    private final LocalDateTime createdAt;
    private volatile LocalDateTime completedAt;
    private String transactionReference;
    private String failureReason;

    public Payment(String bookingId, BigDecimal amount, PaymentMethod method) {
        this.id = "PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getBookingId() {
        return bookingId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void markCompleted(String transactionReference) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionReference = transactionReference;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    @Override
    public String toString() {
        return String.format("Payment[%s | %s | $%.2f | %s | %s]",
                id, method.getDisplayName(), amount, status, createdAt);
    }
}



