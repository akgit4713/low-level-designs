package airline.strategies.payment;

import airline.models.Payment;

import java.math.BigDecimal;

/**
 * Strategy interface for processing payments.
 */
public interface PaymentStrategy {
    
    /**
     * Processes a payment.
     * 
     * @param payment The payment to process
     * @return true if successful
     */
    boolean processPayment(Payment payment);
    
    /**
     * Processes a refund.
     * 
     * @param payment The original payment
     * @param refundAmount The amount to refund
     * @return true if successful
     */
    boolean processRefund(Payment payment, BigDecimal refundAmount);
    
    /**
     * Gets the description of this payment method.
     */
    String getDescription();
}



