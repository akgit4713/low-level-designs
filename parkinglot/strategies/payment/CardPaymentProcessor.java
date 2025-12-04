package parkinglot.strategies.payment;

import parkinglot.models.PaymentResult;
import parkinglot.models.ParkingTicket;

/**
 * Card payment processor implementation.
 * Simulates credit/debit card payment processing.
 */
public class CardPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult processPayment(double amount, ParkingTicket ticket) {
        // In real implementation, this would interact with card terminal
        System.out.println("  💳 Processing card payment of $" + String.format("%.2f", amount));
        
        // Simulate successful payment
        return PaymentResult.success(amount);
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit/Debit Card";
    }
}



