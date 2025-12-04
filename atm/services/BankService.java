package atm.services;

import atm.models.Account;
import atm.models.Card;

import java.math.BigDecimal;

/**
 * Interface for bank backend integration.
 * Follows Dependency Inversion Principle - ATM depends on this abstraction,
 * not concrete bank implementations.
 */
public interface BankService {
    
    /**
     * Validate if card exists and is active.
     */
    boolean validateCard(String cardNumber);
    
    /**
     * Get card details.
     */
    Card getCard(String cardNumber);
    
    /**
     * Validate PIN for a card.
     */
    boolean validatePin(String cardNumber, String pin);
    
    /**
     * Get account associated with a card.
     */
    Account getAccount(String cardNumber);
    
    /**
     * Debit amount from account (for withdrawal).
     * @return true if successful
     */
    boolean debit(String accountNumber, BigDecimal amount);
    
    /**
     * Credit amount to account (for deposit).
     * @return true if successful
     */
    boolean credit(String accountNumber, BigDecimal amount);
    
    /**
     * Block a card due to security reasons.
     */
    void blockCard(String cardNumber);
    
    /**
     * Get account balance.
     */
    BigDecimal getBalance(String accountNumber);
}



