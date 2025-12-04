package splitwise.repositories;

import splitwise.enums.TransactionType;
import splitwise.models.Transaction;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction persistence operations.
 */
public interface TransactionRepository {
    
    /**
     * Save a transaction.
     */
    Transaction save(Transaction transaction);
    
    /**
     * Find a transaction by ID.
     */
    Optional<Transaction> findById(String transactionId);
    
    /**
     * Get all transactions.
     */
    List<Transaction> findAll();
    
    /**
     * Find all transactions where a user is involved (as sender or receiver).
     */
    List<Transaction> findByUserId(String userId);
    
    /**
     * Find transactions between two users.
     */
    List<Transaction> findByUserPair(String userId1, String userId2);
    
    /**
     * Find transactions by type.
     */
    List<Transaction> findByType(TransactionType type);
    
    /**
     * Find transactions by reference ID (expense ID or settlement ID).
     */
    List<Transaction> findByReferenceId(String referenceId);
}



