package splitwise.services;

import splitwise.models.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Service interface for transaction and settlement operations.
 */
public interface TransactionService {
    
    /**
     * Record a settlement payment from one user to another.
     */
    Transaction settleBalance(String fromUserId, String toUserId, BigDecimal amount);
    
    /**
     * Get all transactions for a user.
     */
    List<Transaction> getUserTransactions(String userId);
    
    /**
     * Get transaction history between two users.
     */
    List<Transaction> getTransactionsBetween(String userId1, String userId2);
    
    /**
     * Get a transaction by ID.
     */
    Transaction getTransaction(String transactionId);
    
    /**
     * Get all settlements.
     */
    List<Transaction> getAllSettlements();
}



