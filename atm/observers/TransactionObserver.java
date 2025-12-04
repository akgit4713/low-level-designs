package atm.observers;

import atm.models.Transaction;

/**
 * Observer interface for transaction events.
 * Follows Observer Pattern for decoupled notifications.
 */
public interface TransactionObserver {
    
    /**
     * Called when a transaction is completed.
     */
    void onTransactionComplete(Transaction transaction);
    
    /**
     * Called when a transaction fails.
     */
    void onTransactionFailed(Transaction transaction);
}



