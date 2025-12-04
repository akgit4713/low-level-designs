package splitwise.services;

import splitwise.models.Balance;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Service interface for balance tracking operations.
 */
public interface BalanceService {
    
    /**
     * Update the balance between two users.
     * Positive amount means fromUser owes toUser.
     */
    void updateBalance(String fromUserId, String toUserId, BigDecimal amount);
    
    /**
     * Get the balance between two users.
     * Returns how much user1 owes user2 (positive if user1 owes user2).
     */
    BigDecimal getBalanceBetween(String userId1, String userId2);
    
    /**
     * Get all balances for a user.
     * Returns map of userId -> amount owed (positive means user owes them).
     */
    Map<String, BigDecimal> getUserBalances(String userId);
    
    /**
     * Get the Balance object between two users.
     */
    Balance getBalance(String userId1, String userId2);
    
    /**
     * Check if the balance between two users is settled.
     */
    boolean isSettled(String userId1, String userId2);
    
    /**
     * Get total amount user owes to others.
     */
    BigDecimal getTotalOwed(String userId);
    
    /**
     * Get total amount others owe to user.
     */
    BigDecimal getTotalOwedToUser(String userId);
}



