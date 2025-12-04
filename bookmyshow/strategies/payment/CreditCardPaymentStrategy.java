package bookmyshow.strategies.payment;

import bookmyshow.models.Payment;
import java.util.UUID;

/**
 * Credit card payment strategy.
 * In a real implementation, this would integrate with a payment gateway.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Simulate payment gateway integration
        System.out.println("Processing credit card payment of " + payment.getAmount());
        
        // Simulate processing
        try {
            Thread.sleep(100);  // Simulate network call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        // Simulate successful payment (in real scenario, call payment gateway)
        String transactionId = "CC_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markCompleted(transactionId);
        
        System.out.println("Credit card payment successful. Transaction ID: " + transactionId);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("Processing credit card refund of " + payment.getAmount());
        
        // Simulate refund processing
        payment.markRefunded();
        System.out.println("Credit card refund successful");
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Credit Card";
    }
}



