package carrental.services;

import carrental.enums.PaymentMethod;
import carrental.models.Payment;

import java.util.List;

/**
 * Service interface for payment processing operations.
 */
public interface PaymentService {
    
    /**
     * Processes a payment for a reservation.
     */
    Payment processPayment(String reservationId, PaymentMethod paymentMethod);
    
    /**
     * Processes a refund for a payment.
     */
    boolean processRefund(String paymentId);
    
    /**
     * Gets a payment by its ID.
     */
    Payment getPaymentById(String paymentId);
    
    /**
     * Gets all payments for a reservation.
     */
    List<Payment> getPaymentsByReservation(String reservationId);
    
    /**
     * Gets all payments.
     */
    List<Payment> getAllPayments();
}



