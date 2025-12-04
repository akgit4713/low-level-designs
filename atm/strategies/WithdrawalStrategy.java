package atm.strategies;

import atm.ATM;
import atm.enums.Denomination;
import atm.enums.TransactionStatus;
import atm.enums.TransactionType;
import atm.exceptions.TransactionException;
import atm.models.Account;
import atm.models.Card;
import atm.models.Transaction;

import java.math.BigDecimal;
import java.util.Map;

/**
 * Strategy for cash withdrawal transactions.
 */
public class WithdrawalStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account account, Card card, BigDecimal amount, ATM atm) {
        Transaction transaction = Transaction.builder()
            .accountNumber(account.getAccountNumber())
            .cardNumber(card.getCardNumber())
            .type(TransactionType.WITHDRAWAL)
            .amount(amount)
            .atmId(atm.getAtmId())
            .build();

        // Validate the withdrawal
        if (!validate(account, amount)) {
            if (account.getBalance().compareTo(amount) < 0) {
                transaction.markFailed(TransactionStatus.INSUFFICIENT_FUNDS, "Insufficient balance");
                atm.getTransactionService().recordTransaction(transaction);
                throw new TransactionException("Insufficient funds. Available balance: ₹" + account.getBalance(), 
                    TransactionStatus.INSUFFICIENT_FUNDS);
            }
            if (!account.canWithdraw(amount)) {
                transaction.markFailed(TransactionStatus.LIMIT_EXCEEDED, "Daily limit exceeded");
                atm.getTransactionService().recordTransaction(transaction);
                throw new TransactionException("Daily withdrawal limit exceeded. Remaining limit: ₹" + 
                    account.getRemainingDailyLimit(), TransactionStatus.LIMIT_EXCEEDED);
            }
        }

        // Check ATM cash availability
        if (!atm.getCashDispenser().canDispense(amount)) {
            transaction.markFailed(TransactionStatus.INSUFFICIENT_CASH, "ATM has insufficient cash");
            atm.getTransactionService().recordTransaction(transaction);
            throw new TransactionException("ATM cannot dispense this amount. Please try a different amount.", 
                TransactionStatus.INSUFFICIENT_CASH);
        }

        // Debit from bank account
        boolean debited = atm.getBankService().debit(account.getAccountNumber(), amount);
        if (!debited) {
            transaction.markFailed(TransactionStatus.FAILED, "Bank declined transaction");
            atm.getTransactionService().recordTransaction(transaction);
            throw new TransactionException("Transaction declined by bank. Please try again.", 
                TransactionStatus.FAILED);
        }

        // Dispense cash
        Map<Denomination, Integer> dispensedNotes = atm.getCashDispenser().dispense(amount);
        atm.setLastDispensedNotes(dispensedNotes);

        // Update local account balance (for display)
        account.debit(amount);

        // Mark transaction successful
        transaction.markSuccess(account.getBalance());
        atm.getTransactionService().recordTransaction(transaction);

        // Notify observers
        atm.notifyTransactionComplete(transaction);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  CASH WITHDRAWAL SUCCESSFUL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Amount Withdrawn: ₹" + amount);
        System.out.println("  New Balance     : ₹" + account.getBalance());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Please collect your cash:");
        for (Map.Entry<Denomination, Integer> entry : dispensedNotes.entrySet()) {
            if (entry.getValue() > 0) {
                System.out.println("    ₹" + entry.getKey().getValue() + " x " + entry.getValue());
            }
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return transaction;
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.WITHDRAWAL;
    }

    @Override
    public boolean validate(Account account, BigDecimal amount) {
        if (account == null || amount == null) {
            return false;
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            return false;
        }
        if (amount.remainder(BigDecimal.valueOf(100)).compareTo(BigDecimal.ZERO) != 0) {
            return false; // Must be multiple of 100
        }
        return account.canWithdraw(amount);
    }
}



