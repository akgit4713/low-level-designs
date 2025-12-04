package bookmyshow.strategies.payment;

import bookmyshow.models.Payment;
import java.util.UUID;

/**
 * Digital wallet payment strategy.
 * Simulates wallet payment processing.
 */
public class WalletPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        System.out.println("Processing wallet payment of " + payment.getAmount());
        
        // Simulate wallet deduction
        try {
            Thread.sleep(30);  // Wallets are very fast
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
        
        String transactionId = "WALLET_" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markCompleted(transactionId);
        
        System.out.println("Wallet payment successful. Transaction ID: " + transactionId);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("Processing wallet refund of " + payment.getAmount());
        payment.markRefunded();
        System.out.println("Wallet refund successful - Amount credited back to wallet");
        return true;
    }

    @Override
    public String getPaymentMethodName() {
        return "Digital Wallet";
    }
}



