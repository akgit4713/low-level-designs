package splitwise.observers;

import splitwise.models.Transaction;

/**
 * Observer interface for settlement events.
 * Implementations can react to settlement payments.
 */
public interface SettlementObserver {
    
    /**
     * Called when a settlement payment is made.
     */
    void onSettlement(Transaction settlement);
}



