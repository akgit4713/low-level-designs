package airline.strategies.payment;

import airline.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Credit card payment processing strategy.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Simulate credit card processing
        System.out.println("💳 Processing credit card payment of $" + payment.getAmount());
        
        // Simulate some processing time and validation
        try {
            Thread.sleep(100); // Simulate API call
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
        
        // Generate transaction reference
        String txnRef = "CC-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        payment.markCompleted(txnRef);
        
        System.out.println("✓ Credit card payment successful. Txn: " + txnRef);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment, BigDecimal refundAmount) {
        System.out.println("💳 Processing credit card refund of $" + refundAmount);
        payment.markRefunded();
        System.out.println("✓ Credit card refund processed");
        return true;
    }

    @Override
    public String getDescription() {
        return "Credit Card Payment";
    }
}



