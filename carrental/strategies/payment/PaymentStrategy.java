package carrental.strategies.payment;

import carrental.models.Payment;

/**
 * Strategy interface for processing payments.
 * Follows Strategy Pattern for different payment methods.
 */
public interface PaymentStrategy {
    
    /**
     * Processes a payment.
     * 
     * @param payment The payment to process
     * @return true if payment was successful, false otherwise
     */
    boolean processPayment(Payment payment);
    
    /**
     * Processes a refund for a payment.
     * 
     * @param payment The payment to refund
     * @return true if refund was successful, false otherwise
     */
    boolean processRefund(Payment payment);
    
    /**
     * Returns the name of this payment strategy.
     */
    String getStrategyName();
}



