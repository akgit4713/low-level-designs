package digitalwallet.observers;

import digitalwallet.models.Transaction;

/**
 * Observer interface for transaction lifecycle events.
 * Follows Observer Pattern - decouples event producers from consumers.
 */
public interface TransactionObserver {
    
    /**
     * Called when a new transaction is created
     */
    void onTransactionCreated(Transaction transaction);
    
    /**
     * Called when a transaction is completed successfully
     */
    void onTransactionCompleted(Transaction transaction);
    
    /**
     * Called when a transaction fails
     */
    void onTransactionFailed(Transaction transaction, String reason);
    
    /**
     * Called when a transaction is reversed
     */
    void onTransactionReversed(Transaction transaction);
}



