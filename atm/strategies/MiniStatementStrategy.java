package atm.strategies;

import atm.ATM;
import atm.enums.TransactionType;
import atm.models.Account;
import atm.models.Card;
import atm.models.Transaction;

import java.math.BigDecimal;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Strategy for mini statement transactions.
 */
public class MiniStatementStrategy implements TransactionStrategy {
    
    private static final int MAX_TRANSACTIONS = 5;
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm");

    @Override
    public Transaction execute(Account account, Card card, BigDecimal amount, ATM atm) {
        Transaction transaction = Transaction.builder()
            .accountNumber(account.getAccountNumber())
            .cardNumber(card.getCardNumber())
            .type(TransactionType.MINI_STATEMENT)
            .amount(BigDecimal.ZERO)
            .atmId(atm.getAtmId())
            .build();

        // Get recent transactions
        List<Transaction> recentTransactions = atm.getTransactionService()
            .getRecentTransactions(account.getAccountNumber(), MAX_TRANSACTIONS);

        transaction.markSuccess(account.getBalance());
        atm.getTransactionService().recordTransaction(transaction);

        // Display mini statement
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("                   MINI STATEMENT");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Account: " + account.getAccountNumber());
        System.out.println("  Last " + MAX_TRANSACTIONS + " Transactions:");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        if (recentTransactions.isEmpty()) {
            System.out.println("  No recent transactions found.");
        } else {
            System.out.printf("  %-18s %-15s %12s%n", "DATE", "TYPE", "AMOUNT");
            System.out.println("  ──────────────────────────────────────────────────");
            for (Transaction txn : recentTransactions) {
                String amountStr = formatAmount(txn);
                System.out.printf("  %-18s %-15s %12s%n", 
                    txn.getTimestamp().format(DATE_FORMAT),
                    txn.getType().name(),
                    amountStr);
            }
        }
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Current Balance: ₹" + account.getBalance());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return transaction;
    }

    private String formatAmount(Transaction txn) {
        if (txn.getType() == TransactionType.WITHDRAWAL) {
            return "-₹" + txn.getAmount();
        } else if (txn.getType() == TransactionType.DEPOSIT) {
            return "+₹" + txn.getAmount();
        }
        return "₹" + txn.getAmount();
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.MINI_STATEMENT;
    }

    @Override
    public boolean validate(Account account, BigDecimal amount) {
        return account != null;
    }
}



