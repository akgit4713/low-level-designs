package splitwise.models;

import splitwise.enums.ExpenseStatus;
import splitwise.enums.SplitMethod;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents an expense shared among group members.
 * Uses Builder pattern for flexible construction.
 */
public class Expense {
    private final String id;
    private final String groupId;
    private final String payerId;
    private final BigDecimal amount;
    private final String description;
    private final SplitMethod splitMethod;
    private final List<Split> splits;
    private ExpenseStatus status;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    private Expense(Builder builder) {
        this.id = UUID.randomUUID().toString();
        this.groupId = builder.groupId;
        this.payerId = builder.payerId;
        this.amount = builder.amount;
        this.description = builder.description;
        this.splitMethod = builder.splitMethod;
        this.splits = new ArrayList<>(builder.splits);
        this.status = ExpenseStatus.ACTIVE;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    // Getters
    public String getId() {
        return id;
    }
    
    public String getGroupId() {
        return groupId;
    }
    
    public String getPayerId() {
        return payerId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public String getDescription() {
        return description;
    }
    
    public SplitMethod getSplitMethod() {
        return splitMethod;
    }
    
    public List<Split> getSplits() {
        return Collections.unmodifiableList(splits);
    }
    
    public ExpenseStatus getStatus() {
        return status;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    // Status management
    public void setStatus(ExpenseStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }
    
    public void delete() {
        this.status = ExpenseStatus.DELETED;
        this.updatedAt = LocalDateTime.now();
    }
    
    /**
     * Get participant IDs from splits
     */
    public Set<String> getParticipantIds() {
        Set<String> participantIds = new HashSet<>();
        for (Split split : splits) {
            participantIds.add(split.getUserId());
        }
        return participantIds;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Expense expense = (Expense) o;
        return Objects.equals(id, expense.id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
    
    @Override
    public String toString() {
        return "Expense{" +
                "id='" + id + '\'' +
                ", amount=" + amount +
                ", description='" + description + '\'' +
                ", splitMethod=" + splitMethod +
                ", status=" + status +
                '}';
    }
    
    /**
     * Builder for Expense
     */
    public static class Builder {
        private String groupId;
        private String payerId;
        private BigDecimal amount;
        private String description;
        private SplitMethod splitMethod = SplitMethod.EQUAL;
        private List<Split> splits = new ArrayList<>();
        
        public Builder groupId(String groupId) {
            this.groupId = groupId;
            return this;
        }
        
        public Builder payerId(String payerId) {
            this.payerId = payerId;
            return this;
        }
        
        public Builder amount(BigDecimal amount) {
            this.amount = amount;
            return this;
        }
        
        public Builder description(String description) {
            this.description = description;
            return this;
        }
        
        public Builder splitMethod(SplitMethod splitMethod) {
            this.splitMethod = splitMethod;
            return this;
        }
        
        public Builder splits(List<Split> splits) {
            this.splits = splits;
            return this;
        }
        
        public Expense build() {
            Objects.requireNonNull(groupId, "groupId is required");
            Objects.requireNonNull(payerId, "payerId is required");
            Objects.requireNonNull(amount, "amount is required");
            Objects.requireNonNull(description, "description is required");
            return new Expense(this);
        }
    }
}



