package digitalwallet.services;

import digitalwallet.models.BankAccount;
import digitalwallet.models.CreditCard;
import digitalwallet.models.PaymentMethod;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for payment method management.
 */
public interface PaymentMethodService {
    
    /**
     * Add a credit/debit card
     */
    CreditCard addCreditCard(String userId, String cardNumber, String expiry,
                             String cvv, String cardholderName);
    
    /**
     * Add a bank account
     */
    BankAccount addBankAccount(String userId, String bankName, String routingNumber,
                               String accountNumber, String accountType, String accountHolderName);
    
    /**
     * Get payment method by ID
     */
    Optional<PaymentMethod> getPaymentMethod(String paymentMethodId);
    
    /**
     * Get all payment methods for a user
     */
    List<PaymentMethod> getPaymentMethods(String userId);
    
    /**
     * Get active payment methods for a user
     */
    List<PaymentMethod> getActivePaymentMethods(String userId);
    
    /**
     * Get credit cards for a user
     */
    List<CreditCard> getCreditCards(String userId);
    
    /**
     * Get bank accounts for a user
     */
    List<BankAccount> getBankAccounts(String userId);
    
    /**
     * Remove a payment method
     */
    void removePaymentMethod(String paymentMethodId);
    
    /**
     * Verify a payment method
     */
    void verifyPaymentMethod(String paymentMethodId);
    
    /**
     * Set nickname for a payment method
     */
    void setNickname(String paymentMethodId, String nickname);
}



