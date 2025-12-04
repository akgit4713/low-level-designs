package onlineshopping.services;

import onlineshopping.enums.PaymentMethod;
import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for payment processing
 */
public interface PaymentService {
    
    /**
     * Process payment for an order
     */
    Payment processPayment(Order order, PaymentMethod method);
    
    /**
     * Get payment by ID
     */
    Optional<Payment> getPayment(String paymentId);
    
    /**
     * Get payment for an order
     */
    Optional<Payment> getPaymentByOrder(String orderId);
    
    /**
     * Process refund
     */
    boolean processRefund(String paymentId, BigDecimal amount);
    
    /**
     * Process full refund
     */
    boolean processFullRefund(String paymentId);
    
    /**
     * Validate payment method for order
     */
    boolean validatePaymentMethod(Order order, PaymentMethod method);
}



