package digitalwallet.services;

import digitalwallet.enums.Currency;
import digitalwallet.enums.TransactionType;
import digitalwallet.models.Transaction;
import digitalwallet.models.TransactionStatement;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for transaction management.
 */
public interface TransactionService {
    
    /**
     * Create a transaction record
     */
    Transaction createTransaction(String walletId, TransactionType type, BigDecimal amount,
                                   Currency currency, String description, String referenceId);
    
    /**
     * Get transaction by ID
     */
    Optional<Transaction> getTransaction(String transactionId);
    
    /**
     * Get transactions for a wallet
     */
    List<Transaction> getTransactions(String walletId);
    
    /**
     * Get transactions for a wallet within a date range
     */
    List<Transaction> getTransactions(String walletId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Generate a transaction statement
     */
    TransactionStatement generateStatement(String walletId, LocalDateTime start, LocalDateTime end);
    
    /**
     * Mark transaction as completed
     */
    void completeTransaction(String transactionId);
    
    /**
     * Mark transaction as failed
     */
    void failTransaction(String transactionId, String reason);
    
    /**
     * Reverse a completed transaction
     */
    Transaction reverseTransaction(String transactionId, String reason);
    
    /**
     * Check for duplicate transaction using idempotency key
     */
    Optional<Transaction> findByIdempotencyKey(String idempotencyKey);
}



