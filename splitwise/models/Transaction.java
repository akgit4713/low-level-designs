package splitwise.models;

import splitwise.enums.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.UUID;

/**
 * Represents a financial transaction (expense split or settlement).
 */
public class Transaction {
    private final String id;
    private final String fromUserId;
    private final String toUserId;
    private final BigDecimal amount;
    private final TransactionType type;
    private final String referenceId; // expenseId or settlementId
    private final String description;
    private final LocalDateTime createdAt;
    
    private Transaction(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.fromUserId = builder.fromUserId;
        this.toUserId = builder.toUserId;
        this.amount = builder.amount;
        this.type = builder.type;
        this.referenceId = builder.referenceId;
        this.description = builder.description;
        this.createdAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getFromUserId() {
        return fromUserId;
    }
    
    public String getToUserId() {
        return toUserId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public TransactionType getType() {
        return type;
    }
    
    public String getReferenceId() {
        return referenceId;
    }
    
    public String getDescription() {
        return description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
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
        return "Transaction{" +
                "id='" + id + '\'' +
                ", from='" + fromUserId + '\'' +
                ", to='" + toUserId + '\'' +
                ", amount=" + amount +
                ", type=" + type +
                ", description='" + description + '\'' +
                '}';
    }
    
    /**
     * Builder for Transaction
     */
    public static class Builder {
        private String fromUserId;
        private String toUserId;
        private BigDecimal amount;
        private TransactionType type;
        private String referenceId;
        private String description;
        
        public Builder fromUserId(String fromUserId) {
            this.fromUserId = fromUserId;
            return this;
        }
        
        public Builder toUserId(String toUserId) {
            this.toUserId = toUserId;
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder type(TransactionType type) {
            this.type = type;
            return this;
        }
        
        public Builder referenceId(String referenceId) {
            this.referenceId = referenceId;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Transaction build() {
            Objects.requireNonNull(fromUserId, "fromUserId is required");
            Objects.requireNonNull(toUserId, "toUserId is required");
            Objects.requireNonNull(amount, "amount is required");
            Objects.requireNonNull(type, "type is required");
            return new Transaction(this);
        }
    }
}



