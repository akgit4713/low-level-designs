package airline.strategies.payment;

import airline.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Net banking payment processing strategy.
 */
public class NetBankingPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("🏦 Processing net banking payment of $" + payment.getAmount());
        
        try {
            Thread.sleep(200); // Net banking takes longer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
        
        String txnRef = "NB-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        payment.markCompleted(txnRef);
        
        System.out.println("✓ Net banking payment successful. Txn: " + txnRef);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment, BigDecimal refundAmount) {
        System.out.println("🏦 Processing net banking refund of $" + refundAmount);
        System.out.println("⏳ Net banking refund may take 3-5 business days");
        payment.markRefunded();
        return true;
    }

    @Override
    public String getDescription() {
        return "Net Banking Payment";
    }
}



