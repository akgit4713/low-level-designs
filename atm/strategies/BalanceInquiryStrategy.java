package atm.strategies;

import atm.ATM;
import atm.enums.TransactionType;
import atm.models.Account;
import atm.models.Card;
import atm.models.Transaction;

import java.math.BigDecimal;

/**
 * Strategy for balance inquiry transactions.
 */
public class BalanceInquiryStrategy implements TransactionStrategy {

    @Override
    public Transaction execute(Account account, Card card, BigDecimal amount, ATM atm) {
        BigDecimal balance = account.getBalance();
        
        Transaction transaction = Transaction.builder()
            .accountNumber(account.getAccountNumber())
            .cardNumber(card.getCardNumber())
            .type(TransactionType.BALANCE_INQUIRY)
            .amount(BigDecimal.ZERO)
            .atmId(atm.getAtmId())
            .build();
        
        transaction.markSuccess(balance);
        
        // Log transaction
        atm.getTransactionService().recordTransaction(transaction);
        
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  BALANCE INQUIRY");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  Account Type    : " + account.getAccountType().getDisplayName());
        System.out.println("  Available Balance: ₹" + balance);
        System.out.println("  Daily Limit Left : ₹" + account.getRemainingDailyLimit());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        
        return transaction;
    }

    @Override
    public TransactionType getTransactionType() {
        return TransactionType.BALANCE_INQUIRY;
    }

    @Override
    public boolean validate(Account account, BigDecimal amount) {
        return account != null; // Balance inquiry is always valid if account exists
    }
}



