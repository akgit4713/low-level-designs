package bookmyshow.models;

import bookmyshow.enums.PaymentMethod;
import bookmyshow.enums.PaymentStatus;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a payment transaction.
 */
public class Payment {
    private final String id;
    private final String bookingId;
    private BigDecimal amount;
    private PaymentMethod method;
    private PaymentStatus status;
    private String transactionId;
    private LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String failureReason;

    public Payment(String bookingId, BigDecimal amount, PaymentMethod method) {
        this.id = UUID.randomUUID().toString();
        this.bookingId = bookingId;
        this.amount = amount;
        this.method = method;
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getFailureReason() { return failureReason; }

    // Setters
    public void setAmount(BigDecimal amount) { this.amount = amount; }
    public void setMethod(PaymentMethod method) { this.method = method; }

    // Status transitions
    public void markProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public void markCancelled() {
        this.status = PaymentStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', amount=%s, method=%s, status=%s}", 
            id, amount, method, status);
    }
}



