package carrental.models;

import carrental.enums.PaymentMethod;
import carrental.enums.PaymentStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a payment for a reservation.
 */
public class Payment {
    private final String id;
    private final Reservation reservation;
    private final BigDecimal amount;
    private final PaymentMethod paymentMethod;
    private PaymentStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime processedAt;
    private String transactionReference;

    private Payment(Builder builder) {
        this.id = builder.id;
        this.reservation = builder.reservation;
        this.amount = builder.amount;
        this.paymentMethod = builder.paymentMethod;
        this.status = builder.status;
        this.createdAt = builder.createdAt;
        this.processedAt = builder.processedAt;
        this.transactionReference = builder.transactionReference;
    }

    // Getters
    public String getId() { return id; }
    public Reservation getReservation() { return reservation; }
    public BigDecimal getAmount() { return amount; }
    public PaymentMethod getPaymentMethod() { return paymentMethod; }
    public PaymentStatus getStatus() { return status; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getProcessedAt() { return processedAt; }
    public String getTransactionReference() { return transactionReference; }

    // Status management
    public synchronized void markAsProcessing() {
        this.status = PaymentStatus.PROCESSING;
    }

    public synchronized void markAsCompleted(String transactionReference) {
        this.status = PaymentStatus.COMPLETED;
        this.transactionReference = transactionReference;
        this.processedAt = LocalDateTime.now();
    }

    public synchronized void markAsFailed() {
        this.status = PaymentStatus.FAILED;
        this.processedAt = LocalDateTime.now();
    }

    public synchronized void markAsRefunded() {
        this.status = PaymentStatus.REFUNDED;
        this.processedAt = LocalDateTime.now();
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
        return String.format("Payment{id='%s', reservation=%s, amount=$%.2f, method=%s, status=%s}",
            id, reservation.getId(), amount, paymentMethod.getDisplayName(), status);
    }

    // Builder Pattern
    public static class Builder {
        private String id;
        private Reservation reservation;
        private BigDecimal amount;
        private PaymentMethod paymentMethod;
        private PaymentStatus status = PaymentStatus.PENDING;
        private LocalDateTime createdAt = LocalDateTime.now();
        private LocalDateTime processedAt;
        private String transactionReference;

        public Builder id(String id) { this.id = id; return this; }
        public Builder reservation(Reservation reservation) { this.reservation = reservation; return this; }
        public Builder amount(BigDecimal amount) { this.amount = amount; return this; }
        public Builder paymentMethod(PaymentMethod paymentMethod) { this.paymentMethod = paymentMethod; return this; }
        public Builder status(PaymentStatus status) { this.status = status; return this; }
        public Builder createdAt(LocalDateTime createdAt) { this.createdAt = createdAt; return this; }
        public Builder processedAt(LocalDateTime processedAt) { this.processedAt = processedAt; return this; }
        public Builder transactionReference(String transactionReference) { this.transactionReference = transactionReference; return this; }

        public Payment build() {
            Objects.requireNonNull(id, "Payment ID cannot be null");
            Objects.requireNonNull(reservation, "Reservation cannot be null");
            Objects.requireNonNull(amount, "Amount cannot be null");
            Objects.requireNonNull(paymentMethod, "Payment method cannot be null");
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            return new Payment(this);
        }
    }
}



