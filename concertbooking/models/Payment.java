package concertbooking.models;

import concertbooking.enums.PaymentMethod;
import concertbooking.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment transaction
 */
public class Payment {
    private final String id;
    private final String bookingId;
    private final String userId;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final LocalDateTime createdAt;
    private volatile PaymentStatus status;
    private volatile String transactionId;
    private volatile LocalDateTime processedAt;
    private volatile String failureReason;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.bookingId = builder.bookingId;
        this.userId = builder.userId;
        this.amount = builder.amount;
        this.method = builder.method;
        this.createdAt = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public String getId() { return id; }
    public String getBookingId() { return bookingId; }
    public String getUserId() { return userId; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getMethod() { return method; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public PaymentStatus getStatus() { return status; }
    public String getTransactionId() { return transactionId; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getFailureReason() { return failureReason; }

    public boolean isSuccessful() {
        return status.isSuccessful();
    }

    public boolean isFailed() {
        return status.isFailed();
    }

    public void markProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markCompleted(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
        this.processedAt = LocalDateTime.now();
    }

    public void markRefunded() {
        this.status = PaymentStatus.REFUNDED;
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(id, payment.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', bookingId='%s', amount=%s, method=%s, status=%s}",
            id, bookingId, amount, method, status);
    }

    public static class Builder {
        private String id;
        private String bookingId;
        private String userId;
        private BigDecimal amount;
        private PaymentMethod method;

        public Builder id(String id) { this.id = id; return this; }
        public Builder bookingId(String bookingId) { this.bookingId = bookingId; return this; }
        public Builder userId(String userId) { this.userId = userId; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder method(PaymentMethod method) { this.method = method; return this; }

        public Payment build() {
            Objects.requireNonNull(id, "Payment ID is required");
            Objects.requireNonNull(bookingId, "Booking ID is required");
            Objects.requireNonNull(userId, "User ID is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(method, "Payment method is required");
            return new Payment(this);
        }
    }
}



