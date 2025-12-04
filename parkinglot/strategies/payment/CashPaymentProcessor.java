package parkinglot.strategies.payment;

import parkinglot.models.PaymentResult;
import parkinglot.models.ParkingTicket;

/**
 * Cash payment processor implementation.
 * Simulates cash payment processing.
 */
public class CashPaymentProcessor implements PaymentProcessor {

    @Override
    public PaymentResult processPayment(double amount, ParkingTicket ticket) {
        // In real implementation, this would interact with cash register
        System.out.println("  💵 Processing cash payment of $" + String.format("%.2f", amount));
        return PaymentResult.success(amount);
    }

    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }
}



