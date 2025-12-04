package fooddelivery.strategies.payment;

import fooddelivery.models.Payment;

/**
 * Strategy interface for processing payments.
 * Implements Strategy Pattern for different payment methods.
 */
public interface PaymentStrategy {
    
    /**
     * Process the payment.
     * @param payment Payment to process
     * @return true if payment successful, false otherwise
     */
    boolean processPayment(Payment payment);
    
    /**
     * Refund a completed payment.
     * @param payment Payment to refund
     * @return true if refund successful, false otherwise
     */
    boolean refundPayment(Payment payment);
    
    /**
     * Validate payment details before processing.
     * @param payment Payment to validate
     * @return true if valid, false otherwise
     */
    boolean validatePaymentDetails(Payment payment);
}



