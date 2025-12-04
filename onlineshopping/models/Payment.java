package onlineshopping.models;

import onlineshopping.enums.PaymentMethod;
import onlineshopping.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Optional;

/**
 * Represents a payment transaction
 */
public class Payment {
    private final String id;
    private final String orderId;
    private final String userId;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final LocalDateTime createdAt;
    
    private PaymentStatus status;
    private String transactionId; // External payment gateway transaction ID
    private LocalDateTime completedAt;
    private String failureReason;
    private BigDecimal refundedAmount;

    public Payment(String id, String orderId, String userId, BigDecimal amount, PaymentMethod method) {
        this.id = Objects.requireNonNull(id, "Payment ID is required");
        this.orderId = Objects.requireNonNull(orderId, "Order ID is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.amount = Objects.requireNonNull(amount, "Amount is required");
        this.method = Objects.requireNonNull(method, "Payment method is required");
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
        this.refundedAmount = BigDecimal.ZERO;
    }

    public String getId() {
        return id;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getUserId() {
        return userId;
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

    public Optional<String> getTransactionId() {
        return Optional.ofNullable(transactionId);
    }

    public Optional<LocalDateTime> getCompletedAt() {
        return Optional.ofNullable(completedAt);
    }

    public Optional<String> getFailureReason() {
        return Optional.ofNullable(failureReason);
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    /**
     * Mark payment as processing
     */
    public void startProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    /**
     * Mark payment as completed
     */
    public void complete(String transactionId) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionId = transactionId;
        this.completedAt = LocalDateTime.now();
    }

    /**
     * Mark payment as failed
     */
    public void fail(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    /**
     * Process a refund
     */
    public void refund(BigDecimal refundAmount) {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        
        BigDecimal totalRefund = refundedAmount.add(refundAmount);
        if (totalRefund.compareTo(amount) > 0) {
            throw new IllegalArgumentException("Refund amount exceeds payment amount");
        }
        
        this.refundedAmount = totalRefund;
        if (totalRefund.equals(amount)) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
    }

    /**
     * Check if payment was successful
     */
    public boolean isSuccessful() {
        return status.isSuccessful();
    }

    /**
     * Get remaining amount after refunds
     */
    public BigDecimal getNetAmount() {
        return amount.subtract(refundedAmount);
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
        return String.format("Payment{id='%s', orderId='%s', amount=%s, method=%s, status=%s}", 
            id, orderId, amount, method, status);
    }
}



