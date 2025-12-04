package ridesharing.strategies.payment;

import ridesharing.models.Payment;

/**
 * Strategy interface for processing payments.
 * Follows Strategy Pattern (OCP - can add new payment methods).
 */
public interface PaymentStrategy {
    
    /**
     * Process a payment transaction.
     *
     * @param payment The payment to process
     * @return true if payment was successful, false otherwise
     */
    boolean processPayment(Payment payment);
    
    /**
     * Refund a previously processed payment.
     *
     * @param payment The payment to refund
     * @return true if refund was successful, false otherwise
     */
    boolean refundPayment(Payment payment);
    
    /**
     * Validate if this strategy can handle the given payment.
     */
    boolean canHandle(Payment payment);
    
    /**
     * Get the name of this payment strategy.
     */
    String getStrategyName();
}



