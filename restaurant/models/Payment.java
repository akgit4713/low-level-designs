package restaurant.models;

import restaurant.enums.PaymentMethod;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment transaction
 */
public class Payment {
    private final String id;
    private final Bill bill;
    private final BigDecimal amount;
    private final PaymentMethod method;
    private final LocalDateTime timestamp;
    private final String transactionReference;
    
    private volatile PaymentStatus status;
    private String failureReason;

    public enum PaymentStatus {
        PENDING,
        PROCESSING,
        COMPLETED,
        FAILED,
        REFUNDED
    }

    public Payment(String id, Bill bill, BigDecimal amount, PaymentMethod method, String transactionReference) {
        this.id = Objects.requireNonNull(id, "Payment ID cannot be null");
        this.bill = Objects.requireNonNull(bill, "Bill cannot be null");
        this.amount = Objects.requireNonNull(amount, "Amount cannot be null");
        this.method = Objects.requireNonNull(method, "Payment method cannot be null");
        this.transactionReference = transactionReference;
        this.timestamp = LocalDateTime.now();
        this.status = PaymentStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public Bill getBill() {
        return bill;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public PaymentMethod getMethod() {
        return method;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getTransactionReference() {
        return transactionReference;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public String getFailureReason() {
        return failureReason;
    }

    public void markProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public void markCompleted() {
        this.status = PaymentStatus.COMPLETED;
    }

    public void markFailed(String reason) {
        this.status = PaymentStatus.FAILED;
        this.failureReason = reason;
    }

    public void markRefunded() {
        if (status != PaymentStatus.COMPLETED) {
            throw new IllegalStateException("Can only refund completed payments");
        }
        this.status = PaymentStatus.REFUNDED;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
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
        return String.format("Payment{id='%s', amount=%s, method=%s, status=%s}",
            id, amount, method, status);
    }
}

