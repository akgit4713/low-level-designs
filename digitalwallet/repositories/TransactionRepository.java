package digitalwallet.repositories;

import digitalwallet.enums.TransactionStatus;
import digitalwallet.enums.TransactionType;
import digitalwallet.models.Transaction;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transaction entity.
 */
public interface TransactionRepository extends Repository<Transaction, String> {
    
    /**
     * Find transactions by wallet ID
     */
    List<Transaction> findByWalletId(String walletId);
    
    /**
     * Find transactions by wallet ID within a date range
     */
    List<Transaction> findByWalletIdAndDateRange(String walletId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Find transactions by reference ID (for linked transactions)
     */
    List<Transaction> findByReferenceId(String referenceId);
    
    /**
     * Find transaction by idempotency key
     */
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Find transactions by status
     */
    List<Transaction> findByStatus(TransactionStatus status);
    
    /**
     * Find transactions by wallet and type
     */
    List<Transaction> findByWalletIdAndType(String walletId, TransactionType type);
    
    /**
     * Count transactions for wallet on a given date
     */
    long countByWalletIdAndDate(String walletId, LocalDateTime date);
}



