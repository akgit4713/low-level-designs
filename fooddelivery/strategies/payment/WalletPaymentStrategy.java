package fooddelivery.strategies.payment;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Digital wallet payment processing strategy.
 */
public class WalletPaymentStrategy implements PaymentStrategy {
    
    // Simulated wallet balances (userId -> balance)
    private final Map<String, BigDecimal> walletBalances = new ConcurrentHashMap<>();
    
    public void addBalance(String userId, BigDecimal amount) {
        walletBalances.merge(userId, amount, BigDecimal::add);
    }
    
    public BigDecimal getBalance(String userId) {
        return walletBalances.getOrDefault(userId, BigDecimal.ZERO);
    }

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            payment.markFailed("Invalid payment details");
            return false;
        }
        
        // For demo, we'll use orderId prefix as userId (in real system, get from order)
        String userId = payment.getOrderId().split("-")[0];
        BigDecimal balance = getBalance(userId);
        
        if (balance.compareTo(payment.getAmount()) < 0) {
            payment.markFailed("Insufficient wallet balance");
            return false;
        }
        
        // Deduct from wallet
        walletBalances.put(userId, balance.subtract(payment.getAmount()));
        
        String transactionId = "WAL-" + UUID.randomUUID().toString().substring(0, 8);
        payment.markCompleted(transactionId);
        System.out.println("[Wallet] Payment successful: " + transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        // Refund to wallet
        String userId = payment.getOrderId().split("-")[0];
        addBalance(userId, payment.getAmount());
        
        System.out.println("[Wallet] Refund completed for: " + payment.getTransactionId());
        payment.setStatus(PaymentStatus.REFUNDED);
        return true;
    }

    @Override
    public boolean validatePaymentDetails(Payment payment) {
        return payment != null && 
               payment.getAmount() != null && 
               payment.getAmount().doubleValue() > 0;
    }
}



