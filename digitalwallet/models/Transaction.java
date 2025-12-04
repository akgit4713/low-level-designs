package digitalwallet.models;

import digitalwallet.enums.Currency;
import digitalwallet.enums.TransactionStatus;
import digitalwallet.enums.TransactionType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a financial transaction in the wallet.
 * Immutable core data with mutable status tracking.
 * Uses Builder pattern for construction.
 */
public class Transaction {
    private final String id;
    private final String walletId;
    private final TransactionType type;
    private final BigDecimal amount;
    private final Currency currency;
    private final String description;
    private final String referenceId; // Links related transactions (e.g., transfer pair)
    private final String idempotencyKey;
    private final LocalDateTime createdAt;
    private final BigDecimal balanceAfter;
    
    private volatile TransactionStatus status;
    private volatile LocalDateTime completedAt;
    private volatile String failureReason;

    private Transaction(Builder builder) {
        this.id = builder.id;
        this.walletId = builder.walletId;
        this.type = builder.type;
        this.amount = builder.amount;
        this.currency = builder.currency;
        this.description = builder.description;
        this.referenceId = builder.referenceId;
        this.idempotencyKey = builder.idempotencyKey;
        this.createdAt = LocalDateTime.now();
        this.balanceAfter = builder.balanceAfter;
        this.status = TransactionStatus.PENDING;
        this.completedAt = null;
        this.failureReason = null;
    }

    // Getters
    public String getId() { return id; }
    public String getWalletId() { return walletId; }
    public TransactionType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Currency getCurrency() { return currency; }
    public String getDescription() { return description; }
    public String getReferenceId() { return referenceId; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public BigDecimal getBalanceAfter() { return balanceAfter; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getFailureReason() { return failureReason; }

    // Status transitions
    public void markProcessing() {
        if (!status.canTransitionTo(TransactionStatus.PROCESSING)) {
            throw new IllegalStateException("Cannot transition from " + status + " to PROCESSING");
        }
        this.status = TransactionStatus.PROCESSING;
    }

    public void markCompleted() {
        if (!status.canTransitionTo(TransactionStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot transition from " + status + " to COMPLETED");
        }
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
    }

    public void markFailed(String reason) {
        if (!status.canTransitionTo(TransactionStatus.FAILED)) {
            throw new IllegalStateException("Cannot transition from " + status + " to FAILED");
        }
        this.status = TransactionStatus.FAILED;
        this.failureReason = reason;
        this.completedAt = LocalDateTime.now();
    }

    public void markCancelled() {
        if (!status.canTransitionTo(TransactionStatus.CANCELLED)) {
            throw new IllegalStateException("Cannot transition from " + status + " to CANCELLED");
        }
        this.status = TransactionStatus.CANCELLED;
        this.completedAt = LocalDateTime.now();
    }

    public void markReversed() {
        if (!status.canTransitionTo(TransactionStatus.REVERSED)) {
            throw new IllegalStateException("Cannot transition from " + status + " to REVERSED");
        }
        this.status = TransactionStatus.REVERSED;
        this.completedAt = LocalDateTime.now();
    }

    public boolean isCredit() {
        return type.isCredit();
    }

    public boolean isDebit() {
        return type.isDebit();
    }

    public boolean isSuccessful() {
        return status.isSuccessful();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transaction that = (Transaction) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transaction{id='%s', type=%s, amount=%s, currency=%s, status=%s, desc='%s'}",
            id, type.getDisplayName(), currency.format(amount), currency.name(), 
            status.getDisplayName(), description);
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String walletId;
        private TransactionType type;
        private BigDecimal amount;
        private Currency currency;
        private String description;
        private String referenceId;
        private String idempotencyKey;
        private BigDecimal balanceAfter;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder walletId(String walletId) {
            this.walletId = walletId;
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

        public Builder currency(Currency currency) {
            this.currency = currency;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Builder balanceAfter(BigDecimal balanceAfter) {
            this.balanceAfter = balanceAfter;
            return this;
        }

        public Transaction build() {
            Objects.requireNonNull(id, "Transaction ID is required");
            Objects.requireNonNull(walletId, "Wallet ID is required");
            Objects.requireNonNull(type, "Transaction type is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(currency, "Currency is required");
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            
            return new Transaction(this);
        }
    }
}



