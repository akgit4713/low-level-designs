package atm.observers;

import atm.models.Transaction;

import java.time.format.DateTimeFormatter;

/**
 * Observer that logs all transactions for audit purposes.
 */
public class AuditLogObserver implements TransactionObserver {
    
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void onTransactionComplete(Transaction transaction) {
        log("SUCCESS", transaction);
    }

    @Override
    public void onTransactionFailed(Transaction transaction) {
        log("FAILED", transaction);
    }

    private void log(String status, Transaction transaction) {
        String logEntry = String.format(
            "[AUDIT] %s | TXN: %s | Type: %s | Amount: ₹%s | Account: %s | ATM: %s | Status: %s",
            transaction.getTimestamp().format(FORMATTER),
            transaction.getTransactionId(),
            transaction.getType(),
            transaction.getAmount(),
            transaction.getAccountNumber(),
            transaction.getAtmId(),
            status
        );
        
        // In production, write to audit log file or database
        System.out.println(logEntry);
    }
}



