package atm.services;

import atm.models.Transaction;

import java.time.LocalDate;
import java.util.List;

/**
 * Interface for transaction management.
 */
public interface TransactionService {
    
    /**
     * Record a transaction.
     */
    void recordTransaction(Transaction transaction);
    
    /**
     * Get transaction by ID.
     */
    Transaction getTransaction(String transactionId);
    
    /**
     * Get recent transactions for an account.
     */
    List<Transaction> getRecentTransactions(String accountNumber, int limit);
    
    /**
     * Get all transactions for a date.
     */
    List<Transaction> getTransactionsForDate(String accountNumber, LocalDate date);
    
    /**
     * Get transactions by ATM.
     */
    List<Transaction> getTransactionsByAtm(String atmId, LocalDate date);
}



