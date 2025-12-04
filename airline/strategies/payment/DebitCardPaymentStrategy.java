package airline.strategies.payment;

import airline.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Debit card payment processing strategy.
 */
public class DebitCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("💳 Processing debit card payment of $" + payment.getAmount());
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
        
        String txnRef = "DC-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        payment.markCompleted(txnRef);
        
        System.out.println("✓ Debit card payment successful. Txn: " + txnRef);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment, BigDecimal refundAmount) {
        System.out.println("💳 Processing debit card refund of $" + refundAmount);
        payment.markRefunded();
        System.out.println("✓ Debit card refund processed");
        return true;
    }

    @Override
    public String getDescription() {
        return "Debit Card Payment";
    }
}



