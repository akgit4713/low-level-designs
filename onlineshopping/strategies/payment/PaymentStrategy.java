package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

/**
 * Strategy interface for payment processing
 */
public interface PaymentStrategy {
    
    /**
     * Process payment for an order
     * @param order the order to pay for
     * @param payment the payment object to update
     * @return true if payment was successful
     */
    boolean processPayment(Order order, Payment payment);
    
    /**
     * Validate payment can be processed
     */
    boolean validate(Order order);
    
    /**
     * Process refund for a payment
     */
    boolean processRefund(Payment payment);
}



