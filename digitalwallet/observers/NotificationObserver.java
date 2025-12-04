package digitalwallet.observers;

import digitalwallet.enums.Currency;
import digitalwallet.models.Transaction;
import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import java.math.BigDecimal;

/**
 * Observer that sends notifications to users about wallet and transaction events.
 * In a real implementation, this would integrate with email/SMS/push notification services.
 */
public class NotificationObserver implements TransactionObserver, TransferObserver, WalletObserver {

    @Override
    public void onTransactionCreated(Transaction transaction) {
        // No notification for created - wait for completion
    }

    @Override
    public void onTransactionCompleted(Transaction transaction) {
        sendNotification(transaction.getWalletId(), 
            "Transaction Complete",
            String.format("Your %s of %s has been completed.",
                transaction.getType().getDisplayName(),
                transaction.getCurrency().format(transaction.getAmount()))
        );
    }

    @Override
    public void onTransactionFailed(Transaction transaction, String reason) {
        sendNotification(transaction.getWalletId(),
            "Transaction Failed",
            String.format("Your %s of %s failed: %s",
                transaction.getType().getDisplayName(),
                transaction.getCurrency().format(transaction.getAmount()),
                reason)
        );
    }

    @Override
    public void onTransactionReversed(Transaction transaction) {
        sendNotification(transaction.getWalletId(),
            "Transaction Reversed",
            String.format("Your %s of %s has been reversed.",
                transaction.getType().getDisplayName(),
                transaction.getCurrency().format(transaction.getAmount()))
        );
    }

    @Override
    public void onTransferInitiated(Transfer transfer) {
        // Notify sender
        sendNotification(transfer.getFromWalletId(),
            "Transfer Initiated",
            String.format("Your transfer of %s is being processed.",
                transfer.getSourceCurrency().format(transfer.getAmount()))
        );
    }

    @Override
    public void onTransferCompleted(Transfer transfer) {
        // Notify sender
        sendNotification(transfer.getFromWalletId(),
            "Transfer Sent",
            String.format("Your transfer of %s has been sent successfully.",
                transfer.getSourceCurrency().format(transfer.getAmount()))
        );
        
        // Notify receiver (for P2P transfers)
        if (transfer.getToWalletId() != null) {
            BigDecimal receivedAmount = transfer.getConvertedAmount() != null ? 
                transfer.getConvertedAmount() : transfer.getAmount();
            Currency receivedCurrency = transfer.getTargetCurrency() != null ?
                transfer.getTargetCurrency() : transfer.getSourceCurrency();
                
            sendNotification(transfer.getToWalletId(),
                "Money Received",
                String.format("You received %s.",
                    receivedCurrency.format(receivedAmount))
            );
        }
    }

    @Override
    public void onTransferFailed(Transfer transfer, String reason) {
        sendNotification(transfer.getFromWalletId(),
            "Transfer Failed",
            String.format("Your transfer of %s failed: %s",
                transfer.getSourceCurrency().format(transfer.getAmount()),
                reason)
        );
    }

    @Override
    public void onTransferNeedsReview(Transfer transfer, String reason) {
        sendNotification(transfer.getFromWalletId(),
            "Transfer Under Review",
            String.format("Your transfer of %s is under review. We'll notify you once it's processed.",
                transfer.getSourceCurrency().format(transfer.getAmount()))
        );
    }

    @Override
    public void onWalletCreated(Wallet wallet) {
        sendNotification(wallet.getId(),
            "Welcome to Digital Wallet",
            "Your wallet has been created successfully. Add funds to get started!"
        );
    }

    @Override
    public void onBalanceChanged(Wallet wallet, Currency currency, 
                                  BigDecimal oldBalance, BigDecimal newBalance) {
        // Only notify on significant changes
        BigDecimal change = newBalance.subtract(oldBalance).abs();
        if (change.compareTo(new BigDecimal("100")) >= 0) {
            String changeType = newBalance.compareTo(oldBalance) > 0 ? "increased" : "decreased";
            sendNotification(wallet.getId(),
                "Balance Update",
                String.format("Your %s balance %s by %s. New balance: %s",
                    currency.name(), changeType, 
                    currency.format(change), currency.format(newBalance))
            );
        }
    }

    @Override
    public void onWalletDeactivated(Wallet wallet) {
        sendNotification(wallet.getId(),
            "Wallet Deactivated",
            "Your wallet has been deactivated. Contact support for assistance."
        );
    }

    @Override
    public void onLowBalance(Wallet wallet, Currency currency, BigDecimal balance) {
        sendNotification(wallet.getId(),
            "Low Balance Alert",
            String.format("Your %s balance is low: %s. Consider adding funds.",
                currency.name(), currency.format(balance))
        );
    }

    /**
     * Send notification to a wallet/user.
     * In a real implementation, this would integrate with notification services.
     */
    private void sendNotification(String walletId, String title, String message) {
        // Simulate notification sending
        System.out.println("📬 NOTIFICATION [" + walletId + "]");
        System.out.println("   Title: " + title);
        System.out.println("   Message: " + message);
    }
}



