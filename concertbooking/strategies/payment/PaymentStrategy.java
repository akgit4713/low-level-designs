package concertbooking.strategies.payment;

import concertbooking.models.Payment;

import java.math.BigDecimal;

/**
 * Strategy interface for payment processing
 */
public interface PaymentStrategy {
    
    /**
     * Process the payment
     * @param payment Payment to process
     * @return true if payment successful
     */
    boolean processPayment(Payment payment);
    
    /**
     * Process a refund
     * @param payment Original payment to refund
     * @return true if refund successful
     */
    boolean processRefund(Payment payment);
    
    /**
     * Check if this strategy can process the given amount
     */
    boolean canProcess(BigDecimal amount);
    
    /**
     * Get the processing fee for this payment method
     */
    BigDecimal getProcessingFee(BigDecimal amount);
}



