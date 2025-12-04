package bookmyshow.services;

import bookmyshow.enums.PaymentMethod;
import bookmyshow.models.Payment;
import java.math.BigDecimal;
import java.util.Optional;

/**
 * Service interface for payment processing.
 */
public interface PaymentService {
    
    /**
     * Create and process a payment.
     * @param bookingId Booking ID
     * @param amount Payment amount
     * @param method Payment method
     * @return Processed payment
     */
    Payment processPayment(String bookingId, BigDecimal amount, PaymentMethod method);
    
    /**
     * Process refund for a payment.
     * @param paymentId Payment ID to refund
     * @return Refunded payment
     */
    Payment processRefund(String paymentId);
    
    /**
     * Get payment by ID.
     * @param paymentId Payment ID
     * @return Optional payment
     */
    Optional<Payment> getPayment(String paymentId);
    
    /**
     * Get payment for a booking.
     * @param bookingId Booking ID
     * @return Optional payment
     */
    Optional<Payment> getPaymentByBooking(String bookingId);
}



