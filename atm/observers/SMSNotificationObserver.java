package atm.observers;

import atm.enums.TransactionType;
import atm.models.Transaction;

/**
 * Observer that sends SMS notifications for transactions.
 */
public class SMSNotificationObserver implements TransactionObserver {

    @Override
    public void onTransactionComplete(Transaction transaction) {
        String message = buildMessage(transaction);
        sendSMS(transaction.getCardNumber(), message);
    }

    @Override
    public void onTransactionFailed(Transaction transaction) {
        String message = "Transaction " + transaction.getTransactionId() + 
                         " failed: " + transaction.getFailureReason();
        sendSMS(transaction.getCardNumber(), message);
    }

    private String buildMessage(Transaction transaction) {
        StringBuilder sb = new StringBuilder();
        sb.append("Dear Customer, ");
        
        if (transaction.getType() == TransactionType.WITHDRAWAL) {
            sb.append("₹").append(transaction.getAmount())
              .append(" debited from your A/c ")
              .append(maskAccount(transaction.getAccountNumber()))
              .append(". Available Balance: ₹").append(transaction.getBalanceAfter());
        } else if (transaction.getType() == TransactionType.DEPOSIT) {
            sb.append("₹").append(transaction.getAmount())
              .append(" credited to your A/c ")
              .append(maskAccount(transaction.getAccountNumber()))
              .append(". Available Balance: ₹").append(transaction.getBalanceAfter());
        } else {
            sb.append("Transaction completed successfully. TXN ID: ")
              .append(transaction.getTransactionId());
        }
        
        return sb.toString();
    }

    private String maskAccount(String accountNumber) {
        if (accountNumber.length() <= 4) return accountNumber;
        return "XX" + accountNumber.substring(accountNumber.length() - 4);
    }

    private void sendSMS(String phoneNumber, String message) {
        // In production, integrate with SMS gateway
        System.out.println("[SMS] Sending to " + phoneNumber + ": " + message);
    }
}



