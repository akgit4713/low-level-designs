package hotelmanagement.models;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a payment transaction
 */
public class Payment {
    private final String id;
    private final String billId;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final LocalDateTime processedAt;
    
    private volatile PaymentStatus status;
    private String transactionReference;
    private String failureReason;
    private LocalDateTime refundedAt;
    private BigDecimal refundedAmount;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.billId = builder.billId;
        this.amount = builder.amount;
        this.method = builder.method;
        this.status = builder.status;
        this.transactionReference = builder.transactionReference;
        this.processedAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getBillId() {
        return billId;
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

    public void setStatus(PaymentStatus status) {
        this.status = status;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void setFailureReason(String failureReason) {
        this.failureReason = failureReason;
    }

    public LocalDateTime getRefundedAt() {
        return refundedAt;
    }

    public BigDecimal getRefundedAmount() {
        return refundedAmount;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    /**
     * Mark payment as successful
     */
    public void markSuccessful(String transactionRef) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionReference = transactionRef;
    }

    /**
     * Mark payment as failed
     */
    public void markFailed(String reason) {
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
        this.refundedAmount = refundAmount;
        this.refundedAt = LocalDateTime.now();
        if (refundAmount.compareTo(amount) >= 0) {
            this.status = PaymentStatus.REFUNDED;
        } else {
            this.status = PaymentStatus.PARTIALLY_REFUNDED;
        }
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
        return String.format("Payment{id='%s', bill='%s', amount=%s, method=%s, status=%s}",
            id, billId, amount, method, status);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String billId;
        private BigDecimal amount;
        private PaymentMethod method;
        private PaymentStatus status = PaymentStatus.PENDING;
        private String transactionReference;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder billId(String billId) {
            this.billId = billId;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder method(PaymentMethod method) {
            this.method = method;
            return this;
        }

        public Builder status(PaymentStatus status) {
            this.status = status;
            return this;
        }

        public Builder transactionReference(String transactionReference) {
            this.transactionReference = transactionReference;
            return this;
        }

        public Payment build() {
            if (id == null || id.isBlank()) {
                id = "PAY-" + UUID.randomUUID().toString().substring(0, 8);
            }
            Objects.requireNonNull(billId, "Bill ID is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(method, "Payment method is required");
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Payment amount must be positive");
            }
            
            return new Payment(this);
        }
    }
}



