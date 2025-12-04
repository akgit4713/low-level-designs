package ridesharing.models;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a payment transaction.
 */
public class Payment {
    private final String paymentId;
    private final String rideId;
    private final String passengerId;
    private final double amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime completedAt;
    private String transactionReference;

    public Payment(String rideId, String passengerId, double amount, PaymentMethod paymentMethod) {
        this.paymentId = UUID.randomUUID().toString();
        this.rideId = Objects.requireNonNull(rideId);
        this.passengerId = Objects.requireNonNull(passengerId);
        this.amount = amount;
        this.paymentMethod = Objects.requireNonNull(paymentMethod);
        this.status = PaymentStatus.PENDING;
        this.createdAt = LocalDateTime.now();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public String getRideId() {
        return rideId;
    }

    public String getPassengerId() {
        return passengerId;
    }

    public double getAmount() {
        return amount;
    }

    public PaymentMethod getPaymentMethod() {
        return paymentMethod;
    }

    public PaymentStatus getStatus() {
        return status;
    }

    public void setStatus(PaymentStatus status) {
        this.status = status;
        if (status == PaymentStatus.COMPLETED) {
            this.completedAt = LocalDateTime.now();
        }
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

    public void setTransactionReference(String transactionReference) {
        this.transactionReference = transactionReference;
    }

    public boolean isSuccessful() {
        return status == PaymentStatus.COMPLETED;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Payment payment = (Payment) o;
        return Objects.equals(paymentId, payment.paymentId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(paymentId);
    }

    @Override
    public String toString() {
        return String.format("Payment{id='%s', amount=%.2f, method=%s, status=%s}", 
                paymentId, amount, paymentMethod, status);
    }
}



