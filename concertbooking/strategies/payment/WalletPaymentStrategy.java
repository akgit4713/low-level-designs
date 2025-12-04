package concertbooking.strategies.payment;

import concertbooking.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment strategy for Digital Wallet payments
 */
public class WalletPaymentStrategy implements PaymentStrategy {
    
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("50000.00");
    
    @Override
    public boolean processPayment(Payment payment) {
        if (!canProcess(payment.getAmount())) {
            payment.markFailed("Amount exceeds wallet limit");
            return false;
        }
        
        payment.markProcessing();
        
        try {
            Thread.sleep(50); // Wallets are typically fast
            
            // Simulate 97% success rate
            if (Math.random() > 0.03) {
                String transactionId = "WLT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                payment.markCompleted(transactionId);
                System.out.println("[PAYMENT] Wallet payment processed: " + transactionId);
                return true;
            } else {
                payment.markFailed("Insufficient wallet balance");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        if (!payment.isSuccessful()) {
            return false;
        }
        
        try {
            Thread.sleep(30);
            payment.markRefunded();
            System.out.println("[REFUND] Wallet refund processed for: " + payment.getTransactionId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0 && amount.compareTo(MAX_AMOUNT) <= 0;
    }
    
    @Override
    public BigDecimal getProcessingFee(BigDecimal amount) {
        return BigDecimal.ZERO;
    }
}



