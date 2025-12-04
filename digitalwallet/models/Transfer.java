package digitalwallet.models;

import digitalwallet.enums.Currency;
import digitalwallet.enums.TransactionStatus;
import digitalwallet.enums.TransferType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a fund transfer between wallets or to external accounts.
 * Uses Builder pattern for construction.
 */
public class Transfer {
    private final String id;
    private final String fromWalletId;
    private final String toWalletId; // null for external transfers
    private final String externalAccountId; // for external transfers
    private final TransferType type;
    private final BigDecimal amount;
    private final Currency sourceCurrency;
    private final Currency targetCurrency;
    private final BigDecimal convertedAmount; // after currency conversion
    private final BigDecimal fee;
    private final String description;
    private final String idempotencyKey;
    private final LocalDateTime createdAt;
    
    private volatile TransactionStatus status;
    private volatile LocalDateTime completedAt;
    private volatile String failureReason;
    private volatile String sourceTransactionId;
    private volatile String targetTransactionId;

    private Transfer(Builder builder) {
        this.id = builder.id;
        this.fromWalletId = builder.fromWalletId;
        this.toWalletId = builder.toWalletId;
        this.externalAccountId = builder.externalAccountId;
        this.type = builder.type;
        this.amount = builder.amount;
        this.sourceCurrency = builder.sourceCurrency;
        this.targetCurrency = builder.targetCurrency;
        this.convertedAmount = builder.convertedAmount;
        this.fee = builder.fee != null ? builder.fee : BigDecimal.ZERO;
        this.description = builder.description;
        this.idempotencyKey = builder.idempotencyKey;
        this.createdAt = LocalDateTime.now();
        this.status = TransactionStatus.PENDING;
    }

    // Getters
    public String getId() { return id; }
    public String getFromWalletId() { return fromWalletId; }
    public String getToWalletId() { return toWalletId; }
    public String getExternalAccountId() { return externalAccountId; }
    public TransferType getType() { return type; }
    public BigDecimal getAmount() { return amount; }
    public Currency getSourceCurrency() { return sourceCurrency; }
    public Currency getTargetCurrency() { return targetCurrency; }
    public BigDecimal getConvertedAmount() { return convertedAmount; }
    public BigDecimal getFee() { return fee; }
    public String getDescription() { return description; }
    public String getIdempotencyKey() { return idempotencyKey; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public TransactionStatus getStatus() { return status; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public String getFailureReason() { return failureReason; }
    public String getSourceTransactionId() { return sourceTransactionId; }
    public String getTargetTransactionId() { return targetTransactionId; }

    /**
     * Total amount to be debited from source (amount + fee)
     */
    public BigDecimal getTotalDebitAmount() {
        return amount.add(fee);
    }

    /**
     * Check if this is a cross-currency transfer
     */
    public boolean isCrossCurrency() {
        return !sourceCurrency.equals(targetCurrency);
    }

    /**
     * Check if this is an external transfer
     */
    public boolean isExternalTransfer() {
        return externalAccountId != null;
    }

    // Status transitions
    public void markProcessing() {
        if (!status.canTransitionTo(TransactionStatus.PROCESSING)) {
            throw new IllegalStateException("Cannot transition from " + status + " to PROCESSING");
        }
        this.status = TransactionStatus.PROCESSING;
    }

    public void markCompleted(String sourceTransactionId, String targetTransactionId) {
        if (!status.canTransitionTo(TransactionStatus.COMPLETED)) {
            throw new IllegalStateException("Cannot transition from " + status + " to COMPLETED");
        }
        this.status = TransactionStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.sourceTransactionId = sourceTransactionId;
        this.targetTransactionId = targetTransactionId;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(id, transfer.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Transfer{id='%s', type=%s, amount=%s %s, status=%s}",
            id, type.getDisplayName(), sourceCurrency.format(amount), 
            isCrossCurrency() ? "→ " + targetCurrency.format(convertedAmount) : "",
            status.getDisplayName());
    }

    // Builder
    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private String id;
        private String fromWalletId;
        private String toWalletId;
        private String externalAccountId;
        private TransferType type;
        private BigDecimal amount;
        private Currency sourceCurrency;
        private Currency targetCurrency;
        private BigDecimal convertedAmount;
        private BigDecimal fee;
        private String description;
        private String idempotencyKey;

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder fromWalletId(String fromWalletId) {
            this.fromWalletId = fromWalletId;
            return this;
        }

        public Builder toWalletId(String toWalletId) {
            this.toWalletId = toWalletId;
            return this;
        }

        public Builder externalAccountId(String externalAccountId) {
            this.externalAccountId = externalAccountId;
            return this;
        }

        public Builder type(TransferType type) {
            this.type = type;
            return this;
        }

        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }

        public Builder sourceCurrency(Currency sourceCurrency) {
            this.sourceCurrency = sourceCurrency;
            return this;
        }

        public Builder targetCurrency(Currency targetCurrency) {
            this.targetCurrency = targetCurrency;
            return this;
        }

        public Builder convertedAmount(BigDecimal convertedAmount) {
            this.convertedAmount = convertedAmount;
            return this;
        }

        public Builder fee(BigDecimal fee) {
            this.fee = fee;
            return this;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder idempotencyKey(String idempotencyKey) {
            this.idempotencyKey = idempotencyKey;
            return this;
        }

        public Transfer build() {
            Objects.requireNonNull(id, "Transfer ID is required");
            Objects.requireNonNull(fromWalletId, "Source wallet ID is required");
            Objects.requireNonNull(type, "Transfer type is required");
            Objects.requireNonNull(amount, "Amount is required");
            Objects.requireNonNull(sourceCurrency, "Source currency is required");
            
            if (targetCurrency == null) {
                targetCurrency = sourceCurrency;
            }
            if (convertedAmount == null) {
                convertedAmount = amount;
            }
            
            if (amount.compareTo(BigDecimal.ZERO) <= 0) {
                throw new IllegalArgumentException("Amount must be positive");
            }
            
            // Validate based on transfer type
            if (type.requiresRecipient() && toWalletId == null && externalAccountId == null) {
                throw new IllegalArgumentException("Recipient is required for " + type.getDisplayName());
            }
            
            return new Transfer(this);
        }
    }
}



