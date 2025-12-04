package atm.models;

import atm.enums.TransactionStatus;
import atm.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a transaction performed at the ATM.
 * Immutable after creation (status is the only mutable field).
 */
public class Transaction {
    
    private final String transactionId;
    private final String accountNumber;
    private final String cardNumber;
    private final TransactionType type;
    private final BigDecimal amount;
    private final LocalDateTime timestamp;
    private final String atmId;
    private TransactionStatus status;
    private BigDecimal balanceAfter;
    private String failureReason;

    private Transaction(Builder builder) {
        this.transactionId = builder.transactionId;
        this.accountNumber = builder.accountNumber;
        this.cardNumber = builder.cardNumber;
        this.type = builder.type;
        this.amount = builder.amount;
        this.timestamp = builder.timestamp;
        this.atmId = builder.atmId;
        this.status = builder.status;
        this.balanceAfter = builder.balanceAfter;
        this.failureReason = builder.failureReason;
    }

    public static Builder builder() {
        return new Builder();
    }

    // Getters
    public String getTransactionId() {
        return transactionId;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public TransactionType getType() {
        return type;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public String getAtmId() {
        return atmId;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public BigDecimal getBalanceAfter() {
        return balanceAfter;
    }

    public String getFailureReason() {
        return failureReason;
    }

    // Status updates
    public void markSuccess(BigDecimal balanceAfter) {
        this.status = TransactionStatus.SUCCESS;
        this.balanceAfter = balanceAfter;
    }

    public void markFailed(TransactionStatus status, String reason) {
        this.status = status;
        this.failureReason = reason;
    }

    public void markCancelled() {
        this.status = TransactionStatus.CANCELLED;
    }

    public boolean isSuccessful() {
        return status == TransactionStatus.SUCCESS;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return transactionId.equals(that.transactionId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(transactionId);
    }

    @Override
    public String toString() {
        return "Transaction{" +
               "id='" + transactionId + '\'' +
               ", type=" + type +
               ", amount=" + amount +
               ", status=" + status +
               ", timestamp=" + timestamp +
               '}';
    }

    public static class Builder {
        private String transactionId = "TXN-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        private String accountNumber;
        private String cardNumber;
        private TransactionType type;
        private BigDecimal amount = BigDecimal.ZERO;
        private LocalDateTime timestamp = LocalDateTime.now();
        private String atmId;
        private TransactionStatus status = TransactionStatus.PENDING;
        private BigDecimal balanceAfter;
        private String failureReason;

        public Builder transactionId(String transactionId) {
            this.transactionId = transactionId;
            return this;
        }

        public Builder accountNumber(String accountNumber) {
            this.accountNumber = accountNumber;
            return this;
        }

        public Builder cardNumber(String cardNumber) {
            this.cardNumber = cardNumber;
            return this;
        }

        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder timestamp(LocalDateTime timestamp) {
            this.timestamp = timestamp;
            return this;
        }

        public Builder atmId(String atmId) {
            this.atmId = atmId;
            return this;
        }

        public Builder status(TransactionStatus status) {
            this.status = status;
            return this;
        }

        public Builder balanceAfter(BigDecimal balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Transaction build() {
            Objects.requireNonNull(accountNumber, "Account number is required");
            Objects.requireNonNull(cardNumber, "Card number is required");
            Objects.requireNonNull(type, "Transaction type is required");
            Objects.requireNonNull(atmId, "ATM ID is required");
            return new Transaction(this);
        }
    }
}



