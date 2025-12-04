package concertbooking.services;

import concertbooking.enums.PaymentMethod;
import concertbooking.models.Payment;
import concertbooking.strategies.payment.PaymentStrategy;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for processing payments
 */
public interface PaymentService {
    
    /**
     * Process payment for a booking
     * @param bookingId Booking ID
     * @param userId User ID
     * @param amount Payment amount
     * @param method Payment method
     * @return Payment result
     */
    Payment processPayment(String bookingId, String userId, BigDecimal amount, PaymentMethod method);
    
    /**
     * Process a refund
     * @param paymentId Original payment ID
     * @return true if refund successful
     */
    boolean processRefund(String paymentId);
    
    /**
     * Get payment by ID
     */
    Optional<Payment> getPayment(String paymentId);
    
    /**
     * Register a payment strategy
     */
    void registerPaymentStrategy(PaymentMethod method, PaymentStrategy strategy);
    
    /**
     * Get available payment methods
     */
    PaymentMethod[] getAvailablePaymentMethods();
    
    /**
     * Get processing fee for a payment method
     */
    BigDecimal getProcessingFee(PaymentMethod method, BigDecimal amount);
}



