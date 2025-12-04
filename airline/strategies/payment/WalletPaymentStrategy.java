package airline.strategies.payment;

import airline.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Digital wallet payment processing strategy.
 */
public class WalletPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("💰 Processing wallet payment of $" + payment.getAmount());
        
        try {
            Thread.sleep(50); // Wallet payments are faster
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
        
        String txnRef = "WLT-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        payment.markCompleted(txnRef);
        
        System.out.println("✓ Wallet payment successful. Txn: " + txnRef);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment, BigDecimal refundAmount) {
        System.out.println("💰 Processing wallet refund of $" + refundAmount);
        payment.markRefunded();
        System.out.println("✓ Wallet refund processed (instant)");
        return true;
    }

    @Override
    public String getDescription() {
        return "Digital Wallet Payment";
    }
}



