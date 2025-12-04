package atm.services;

/**
 * Interface for authentication operations.
 */
public interface AuthenticationService {
    
    /**
     * Validate PIN for a card.
     * @param cardNumber The card number
     * @param pin The PIN entered by user
     * @return true if PIN is correct
     */
    boolean validatePin(String cardNumber, String pin);
    
    /**
     * Check if card is blocked.
     */
    boolean isCardBlocked(String cardNumber);
    
    /**
     * Block a card.
     */
    void blockCard(String cardNumber);
    
    /**
     * Record failed authentication attempt.
     * @return remaining attempts
     */
    int recordFailedAttempt(String cardNumber);
    
    /**
     * Reset failed attempts after successful authentication.
     */
    void resetFailedAttempts(String cardNumber);
}



