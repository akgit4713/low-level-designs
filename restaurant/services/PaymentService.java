package restaurant.services;

import restaurant.enums.PaymentMethod;
import restaurant.models.Bill;
import restaurant.models.Payment;
import restaurant.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for payment processing
 */
public interface PaymentService {
    
    /**
     * Process payment for a bill
     */
    Payment processPayment(Bill bill, BigDecimal amount, PaymentMethod method);
    
    /**
     * Get payment by ID
     */
    Optional<Payment> getPayment(String paymentId);
    
    /**
     * Process refund
     */
    boolean processRefund(String paymentId);
    
    /**
     * Register payment strategy for a method
     */
    void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy);
    
    /**
     * Get available payment methods
     */
    PaymentMethod[] getAvailablePaymentMethods();
}

