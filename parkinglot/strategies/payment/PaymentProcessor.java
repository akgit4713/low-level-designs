package parkinglot.strategies.payment;

import parkinglot.models.PaymentResult;
import parkinglot.models.ParkingTicket;

/**
 * Interface for processing payments at exit gates.
 * Implements the Strategy Pattern for different payment methods.
 * 
 * Extension point: Implement for cash, card, mobile payments, etc.
 */
public interface PaymentProcessor {
    
    /**
     * Processes a payment for the parking session.
     * 
     * @param amount The amount to charge
     * @param ticket The parking ticket for reference
     * @return PaymentResult indicating success or failure
     */
    PaymentResult processPayment(double amount, ParkingTicket ticket);
    
    /**
     * Gets the name of this payment method.
     */
    String getPaymentMethodName();
}



