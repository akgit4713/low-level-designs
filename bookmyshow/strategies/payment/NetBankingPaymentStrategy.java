package bookmyshow.strategies.payment;

import bookmyshow.models.Payment;
import java.util.UUID;

/**
 * Net banking payment strategy.
 * Simulates net banking payment processing.
 */
public class NetBankingPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("Processing net banking payment of " + payment.getAmount());
        
        // Simulate bank redirect and processing
        try {
            Thread.sleep(200);  // Net banking takes longer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        String transactionId = "NB_" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
        payment.markCompleted(transactionId);
        
        System.out.println("Net banking payment successful. Transaction ID: " + transactionId);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("Processing net banking refund of " + payment.getAmount());
        System.out.println("Refund will be credited to bank account in 5-7 business days");
        payment.markRefunded();
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Net Banking";
    }
}



