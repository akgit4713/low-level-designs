package ridesharing.services;

import ridesharing.enums.PaymentMethod;
import ridesharing.models.Payment;
import ridesharing.models.Ride;

import java.util.Optional;

/**
 * Service interface for payment processing.
 */
public interface PaymentService {
    
    /**
     * Process payment for a completed ride.
     */
    Payment processPayment(Ride ride, PaymentMethod paymentMethod);
    
    /**
     * Refund a payment.
     */
    boolean refundPayment(String paymentId);
    
    /**
     * Get payment for a ride.
     */
    Optional<Payment> getPaymentForRide(String rideId);
    
    /**
     * Add funds to wallet.
     */
    void addWalletBalance(String passengerId, double amount);
    
    /**
     * Get wallet balance.
     */
    double getWalletBalance(String passengerId);
}



