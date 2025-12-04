package digitalwallet.observers;

import digitalwallet.models.Transfer;

/**
 * Observer interface for transfer lifecycle events.
 */
public interface TransferObserver {
    
    /**
     * Called when a new transfer is initiated
     */
    void onTransferInitiated(Transfer transfer);
    
    /**
     * Called when a transfer is completed successfully
     */
    void onTransferCompleted(Transfer transfer);
    
    /**
     * Called when a transfer fails
     */
    void onTransferFailed(Transfer transfer, String reason);
    
    /**
     * Called when a transfer requires manual review
     */
    void onTransferNeedsReview(Transfer transfer, String reason);
}



