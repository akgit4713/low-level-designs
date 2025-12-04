package ridesharing.strategies.payment;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.PaymentStatus;
import ridesharing.models.Payment;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Payment strategy for in-app wallet payments.
 * Maintains wallet balances in-memory (would be database in production).
 */
public class WalletPaymentStrategy implements PaymentStrategy {
    
    // Simple in-memory wallet balances (passengerId -> balance)
    private final Map<String, Double> walletBalances = new ConcurrentHashMap<>();

    public void addBalance(String passengerId, double amount) {
        walletBalances.merge(passengerId, amount, Double::sum);
    }

    public double getBalance(String passengerId) {
        return walletBalances.getOrDefault(passengerId, 0.0);
    }

    @Override
    public boolean processPayment(Payment payment) {
        String passengerId = payment.getPassengerId();
        double balance = getBalance(passengerId);
        
        if (balance < payment.getAmount()) {
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }
        
        // Deduct from wallet
        walletBalances.compute(passengerId, (id, current) -> 
                (current == null ? 0 : current) - payment.getAmount());
        
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionReference("WALLET-" + UUID.randomUUID().toString().substring(0, 8));
        return true;
    }

    @Override
    public boolean refundPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        // Add back to wallet
        addBalance(payment.getPassengerId(), payment.getAmount());
        payment.setStatus(PaymentStatus.REFUNDED);
        return true;
    }

    @Override
    public boolean canHandle(Payment payment) {
        return payment.getPaymentMethod() == PaymentMethod.WALLET;
    }

    @Override
    public String getStrategyName() {
        return "Wallet Payment";
    }
}



