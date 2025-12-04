package splitwise.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents the balance between two users.
 * A positive amount means user1 owes user2.
 * A negative amount means user2 owes user1.
 */
public class Balance {
    private final String user1Id;
    private final String user2Id;
    private BigDecimal amount;
    
    public Balance(String user1Id, String user2Id) {
        // Ensure consistent ordering for the pair
        if (user1Id.compareTo(user2Id) <= 0) {
            this.user1Id = user1Id;
            this.user2Id = user2Id;
        } else {
            this.user1Id = user2Id;
            this.user2Id = user1Id;
        }
        this.amount = BigDecimal.ZERO;
    }
    
    public Balance(String user1Id, String user2Id, BigDecimal amount) {
        // Ensure consistent ordering for the pair
        if (user1Id.compareTo(user2Id) <= 0) {
            this.user1Id = user1Id;
            this.user2Id = user2Id;
            this.amount = amount;
        } else {
            this.user1Id = user2Id;
            this.user2Id = user1Id;
            this.amount = amount.negate();
        }
    }
    
    public String getUser1Id() {
        return user1Id;
    }
    
    public String getUser2Id() {
        return user2Id;
    }
    
    public BigDecimal getAmount() {
        return amount;
    }
    
    /**
     * Get the key for this balance pair
     */
    public String getKey() {
        return user1Id + ":" + user2Id;
    }
    
    /**
     * Update the balance. Positive means user1 owes user2 more.
     */
    public void updateBalance(BigDecimal delta) {
        this.amount = this.amount.add(delta);
    }
    
    /**
     * Get how much fromUser owes toUser.
     * Returns positive if fromUser owes toUser.
     * Returns negative if toUser owes fromUser.
     */
    public BigDecimal getAmountOwed(String fromUserId, String toUserId) {
        if (fromUserId.equals(user1Id) && toUserId.equals(user2Id)) {
            return amount;
        } else if (fromUserId.equals(user2Id) && toUserId.equals(user1Id)) {
            return amount.negate();
        }
        throw new IllegalArgumentException("Invalid user IDs for this balance");
    }
    
    /**
     * Check if the balance is settled (zero)
     */
    public boolean isSettled() {
        return amount.compareTo(BigDecimal.ZERO) == 0;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Balance balance = (Balance) o;
        return Objects.equals(user1Id, balance.user1Id) &&
               Objects.equals(user2Id, balance.user2Id);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(user1Id, user2Id);
    }
    
    @Override
    public String toString() {
        if (amount.compareTo(BigDecimal.ZERO) > 0) {
            return user1Id + " owes " + user2Id + ": " + amount;
        } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
            return user2Id + " owes " + user1Id + ": " + amount.abs();
        } else {
            return user1Id + " and " + user2Id + " are settled";
        }
    }
}



