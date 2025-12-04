package restaurant.strategies.payment;

import restaurant.models.Bill;
import restaurant.models.Payment;

import java.math.BigDecimal;

/**
 * Strategy interface for processing different payment methods
 * Follows Strategy Pattern - allows adding new payment methods without modifying existing code
 */
public interface PaymentStrategy {
    
    /**
     * Process a payment
     * @param bill The bill to pay
     * @param amount The amount being paid
     * @return Payment result with transaction details
     */
    Payment processPayment(Bill bill, BigDecimal amount);
    
    /**
     * Validate if payment can be processed
     * @param amount The amount to validate
     * @return true if payment can be processed
     */
    boolean canProcess(BigDecimal amount);
    
    /**
     * Get the payment method name
     */
    String getPaymentMethodName();
    
    /**
     * Process a refund
     * @param payment The original payment to refund
     * @return true if refund successful
     */
    boolean processRefund(Payment payment);
}

