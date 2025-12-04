package digitalwallet.observers;

import digitalwallet.enums.Currency;
import digitalwallet.models.Transaction;
import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Observer that logs all wallet and transaction events for audit purposes.
 * Implements multiple observer interfaces.
 */
public class AuditLogObserver implements TransactionObserver, TransferObserver, WalletObserver {
    
    private static final DateTimeFormatter TIMESTAMP_FORMAT = 
        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    
    private final boolean verbose;

    public AuditLogObserver() {
        this(true);
    }

    public AuditLogObserver(boolean verbose) {
        this.verbose = verbose;
    }

    private void log(String level, String category, String message) {
        String timestamp = LocalDateTime.now().format(TIMESTAMP_FORMAT);
        System.out.printf("[%s] [%s] [%s] %s%n", timestamp, level, category, message);
    }

    // TransactionObserver implementation
    @Override
    public void onTransactionCreated(Transaction transaction) {
        if (verbose) {
            log("INFO", "TRANSACTION", String.format(
                "Created: id=%s, type=%s, amount=%s, wallet=%s",
                transaction.getId(), transaction.getType().getDisplayName(),
                transaction.getCurrency().format(transaction.getAmount()),
                transaction.getWalletId()
            ));
        }
    }

    @Override
    public void onTransactionCompleted(Transaction transaction) {
        log("INFO", "TRANSACTION", String.format(
            "Completed: id=%s, type=%s, amount=%s",
            transaction.getId(), transaction.getType().getDisplayName(),
            transaction.getCurrency().format(transaction.getAmount())
        ));
    }

    @Override
    public void onTransactionFailed(Transaction transaction, String reason) {
        log("ERROR", "TRANSACTION", String.format(
            "Failed: id=%s, type=%s, reason=%s",
            transaction.getId(), transaction.getType().getDisplayName(), reason
        ));
    }

    @Override
    public void onTransactionReversed(Transaction transaction) {
        log("WARN", "TRANSACTION", String.format(
            "Reversed: id=%s, type=%s, amount=%s",
            transaction.getId(), transaction.getType().getDisplayName(),
            transaction.getCurrency().format(transaction.getAmount())
        ));
    }

    // TransferObserver implementation
    @Override
    public void onTransferInitiated(Transfer transfer) {
        if (verbose) {
            log("INFO", "TRANSFER", String.format(
                "Initiated: id=%s, from=%s, to=%s, amount=%s",
                transfer.getId(), transfer.getFromWalletId(),
                transfer.getToWalletId() != null ? transfer.getToWalletId() : "EXTERNAL",
                transfer.getSourceCurrency().format(transfer.getAmount())
            ));
        }
    }

    @Override
    public void onTransferCompleted(Transfer transfer) {
        log("INFO", "TRANSFER", String.format(
            "Completed: id=%s, amount=%s, fee=%s",
            transfer.getId(),
            transfer.getSourceCurrency().format(transfer.getAmount()),
            transfer.getSourceCurrency().format(transfer.getFee())
        ));
    }

    @Override
    public void onTransferFailed(Transfer transfer, String reason) {
        log("ERROR", "TRANSFER", String.format(
            "Failed: id=%s, reason=%s",
            transfer.getId(), reason
        ));
    }

    @Override
    public void onTransferNeedsReview(Transfer transfer, String reason) {
        log("WARN", "TRANSFER", String.format(
            "Needs Review: id=%s, amount=%s, reason=%s",
            transfer.getId(),
            transfer.getSourceCurrency().format(transfer.getAmount()),
            reason
        ));
    }

    // WalletObserver implementation
    @Override
    public void onWalletCreated(Wallet wallet) {
        log("INFO", "WALLET", String.format(
            "Created: id=%s, userId=%s",
            wallet.getId(), wallet.getUserId()
        ));
    }

    @Override
    public void onBalanceChanged(Wallet wallet, Currency currency, 
                                  BigDecimal oldBalance, BigDecimal newBalance) {
        if (verbose) {
            BigDecimal change = newBalance.subtract(oldBalance);
            String changeSign = change.compareTo(BigDecimal.ZERO) >= 0 ? "+" : "";
            log("INFO", "WALLET", String.format(
                "Balance changed: wallet=%s, currency=%s, %s%s (now %s)",
                wallet.getId(), currency.name(),
                changeSign, currency.format(change),
                currency.format(newBalance)
            ));
        }
    }

    @Override
    public void onWalletDeactivated(Wallet wallet) {
        log("WARN", "WALLET", String.format(
            "Deactivated: id=%s, userId=%s",
            wallet.getId(), wallet.getUserId()
        ));
    }

    @Override
    public void onLowBalance(Wallet wallet, Currency currency, BigDecimal balance) {
        log("WARN", "WALLET", String.format(
            "Low balance alert: wallet=%s, currency=%s, balance=%s",
            wallet.getId(), currency.name(), currency.format(balance)
        ));
    }
}



