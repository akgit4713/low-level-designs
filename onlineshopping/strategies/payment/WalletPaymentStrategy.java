package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

/**
 * Payment strategy for wallet payments
 */
public class WalletPaymentStrategy implements PaymentStrategy {
    
    // Simulated wallet balances
    private final Map<String, BigDecimal> walletBalances = new ConcurrentHashMap<>();

    public WalletPaymentStrategy() {
        // Initialize with some test balances
        walletBalances.put("default", new BigDecimal("10000"));
    }

    /**
     * Add balance to a wallet (for testing)
     */
    public void addBalance(String userId, BigDecimal amount) {
        walletBalances.merge(userId, amount, BigDecimal::add);
    }

    @Override
    public boolean processPayment(Order order, Payment payment) {
        try {
            payment.startProcessing();
            
            String userId = order.getUserId();
            BigDecimal balance = walletBalances.getOrDefault(userId, 
                walletBalances.getOrDefault("default", BigDecimal.ZERO));
            
            if (balance.compareTo(order.getTotalAmount()) < 0) {
                payment.fail("Insufficient wallet balance");
                return false;
            }
            
            // Deduct from wallet
            walletBalances.put(userId, balance.subtract(order.getTotalAmount()));
            
            String transactionId = "WAL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.complete(transactionId);
            
            System.out.printf("[WALLET] Payment of $%s processed. New balance: $%s%n",
                order.getTotalAmount(), walletBalances.get(userId));
            
            return true;
        } catch (Exception e) {
            payment.fail("Wallet payment failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validate(Order order) {
        BigDecimal balance = walletBalances.getOrDefault(order.getUserId(),
            walletBalances.getOrDefault("default", BigDecimal.ZERO));
        return balance.compareTo(order.getTotalAmount()) >= 0;
    }

    @Override
    public boolean processRefund(Payment payment) {
        try {
            // Add back to wallet
            walletBalances.merge(payment.getUserId(), payment.getAmount(), BigDecimal::add);
            payment.refund(payment.getAmount());
            
            System.out.printf("[WALLET] Refund of $%s credited to wallet%n", payment.getAmount());
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}



