package splitwise.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an individual user's share in an expense.
 */
public class Split {
    private final String userId;
    private final BigDecimal amount;
    private final BigDecimal percentage; // For percentage-based splits
    
    public Split(String userId, BigDecimal amount) {
        this(userId, amount, null);
    }
    
    public Split(String userId, BigDecimal amount, BigDecimal percentage) {
        this.userId = userId;
        this.amount = amount;
        this.percentage = percentage;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    public BigDecimal getPercentage() {
        return percentage;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Split split = (Split) o;
        return Objects.equals(userId, split.userId) &&
               Objects.equals(amount, split.amount);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(userId, amount);
    }
    
    @Override
    public String toString() {
        return "Split{" +
                "userId='" + userId + '\'' +
                ", amount=" + amount +
                (percentage != null ? ", percentage=" + percentage + "%" : "") +
                '}';
    }
}



