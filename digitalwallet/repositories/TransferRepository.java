package digitalwallet.repositories;

import digitalwallet.enums.TransactionStatus;
import digitalwallet.models.Transfer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Transfer entity.
 */
public interface TransferRepository extends Repository<Transfer, String> {
    
    /**
     * Find transfers by source wallet ID
     */
    List<Transfer> findByFromWalletId(String walletId);
    
    /**
     * Find transfers by destination wallet ID
     */
    List<Transfer> findByToWalletId(String walletId);
    
    /**
     * Find all transfers involving a wallet (as sender or receiver)
     */
    List<Transfer> findByWalletId(String walletId);
    
    /**
     * Find transfer by idempotency key
     */
    Optional<Transfer> findByIdempotencyKey(String idempotencyKey);
    
    /**
     * Find transfers by status
     */
    List<Transfer> findByStatus(TransactionStatus status);
    
    /**
     * Find transfers within a date range
     */
    List<Transfer> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find pending transfers older than a specific time
     */
    List<Transfer> findPendingOlderThan(LocalDateTime cutoff);
}



