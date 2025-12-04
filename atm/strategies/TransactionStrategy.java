package atm.strategies;

import atm.ATM;
import atm.enums.TransactionType;
import atm.models.Account;
import atm.models.Card;
import atm.models.Transaction;

import java.math.BigDecimal;

/**
 * Strategy interface for different transaction types.
 * Follows Strategy Pattern - allows adding new transaction types without modifying existing code.
 */
public interface TransactionStrategy {
    
    /**
     * Execute the transaction.
     * @param account The account to perform transaction on
     * @param card The card used for transaction
     * @param amount The transaction amount (may be null for balance inquiry)
     * @param atm The ATM instance
     * @return Transaction record
     */
    Transaction execute(Account account, Card card, BigDecimal amount, ATM atm);
    
    /**
     * Get the transaction type this strategy handles.
     */
    TransactionType getTransactionType();
    
    /**
     * Validate if transaction can be performed.
     * @param account The account
     * @param amount The amount
     * @return true if transaction is valid
     */
    boolean validate(Account account, BigDecimal amount);
}



