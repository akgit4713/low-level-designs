package hotelmanagement.services;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Payment;
import hotelmanagement.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for payment processing
 */
public interface PaymentService {
    
    /**
     * Process a payment for a bill
     * @param bill The bill to pay
     * @param amount The payment amount
     * @param method The payment method
     * @return The payment result
     */
    Payment processPayment(Bill bill, BigDecimal amount, PaymentMethod method);
    
    /**
     * Get a payment by ID
     */
    Optional<Payment> getPayment(String paymentId);
    
    /**
     * Process a refund for a payment
     * @param paymentId The payment ID to refund
     * @param amount The refund amount
     * @return true if refund was successful
     */
    boolean processRefund(String paymentId, BigDecimal amount);
    
    /**
     * Register a payment strategy for a payment method
     */
    void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy);
    
    /**
     * Check if a payment method is supported
     */
    boolean isPaymentMethodSupported(PaymentMethod method);
}



