package atm.strategies;

import atm.ATM;
import atm.enums.TransactionStatus;
import atm.enums.TransactionType;
import atm.exceptions.TransactionException;
import atm.models.Account;
import atm.models.Card;
import atm.models.Transaction;

import java.math.BigDecimal;

/**
 * Strategy for cash deposit transactions.
 */
public class DepositStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account account, Card card, BigDecimal amount, ATM atm) {
        Transaction transaction = Transaction.builder()
            .accountNumber(account.getAccountNumber())
            .cardNumber(card.getCardNumber())
            .type(TransactionType.DEPOSIT)
            .amount(amount)
            .atmId(atm.getAtmId())
            .build();

        // Validate the deposit
        if (!validate(account, amount)) {
            transaction.markFailed(TransactionStatus.FAILED, "Invalid deposit amount");
            atm.getTransactionService().recordTransaction(transaction);
            throw new TransactionException("Invalid deposit amount", TransactionStatus.FAILED);
        }

        // Credit to bank account
        boolean credited = atm.getBankService().credit(account.getAccountNumber(), amount);
        if (!credited) {
            transaction.markFailed(TransactionStatus.FAILED, "Bank declined deposit");
            atm.getTransactionService().recordTransaction(transaction);
            throw new TransactionException("Deposit declined by bank. Please try again.", 
                TransactionStatus.FAILED);
        }

        // Update local account balance
        account.credit(amount);

        // Mark transaction successful
        transaction.markSuccess(account.getBalance());
        atm.getTransactionService().recordTransaction(transaction);

        // Notify observers
        atm.notifyTransactionComplete(transaction);

        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  CASH DEPOSIT SUCCESSFUL");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Amount Deposited: ₹" + amount);
        System.out.println("  New Balance     : ₹" + account.getBalance());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        return transaction;
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.DEPOSIT;
    }

    @Override
    public boolean validate(Account account, BigDecimal amount) {
        if (account == null || amount == null) {
            return false;
        }
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
}



