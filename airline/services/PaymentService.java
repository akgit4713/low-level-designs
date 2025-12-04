package airline.services;

import airline.enums.PaymentMethod;
import airline.models.Booking;
import airline.models.Payment;

import java.math.BigDecimal;

/**
 * Service interface for payment processing.
 */
public interface PaymentService {
    
    /**
     * Processes a payment for a booking.
     */
    Payment processPayment(Booking booking, PaymentMethod method);
    
    /**
     * Processes a refund for a cancelled booking.
     */
    Payment processRefund(Booking booking);
    
    /**
     * Calculates the total price for a booking.
     */
    BigDecimal calculateBookingPrice(Booking booking);
    
    /**
     * Gets payment by ID.
     */
    Payment getPayment(String paymentId);
}



