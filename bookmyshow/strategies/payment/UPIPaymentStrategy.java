package bookmyshow.strategies.payment;

import bookmyshow.models.Payment;
import java.util.UUID;

/**
 * UPI payment strategy.
 * Simulates UPI payment processing.
 */
public class UPIPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("Processing UPI payment of " + payment.getAmount());
        
        // Simulate UPI payment
        try {
            Thread.sleep(50);  // UPI is typically faster
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        String transactionId = "UPI_" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        payment.markCompleted(transactionId);
        
        System.out.println("UPI payment successful. Transaction ID: " + transactionId);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("Processing UPI refund of " + payment.getAmount());
        payment.markRefunded();
        System.out.println("UPI refund successful");
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "UPI";
    }
}



