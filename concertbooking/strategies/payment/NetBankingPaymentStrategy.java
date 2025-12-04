package concertbooking.strategies.payment;

import concertbooking.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment strategy for Net Banking payments
 */
public class NetBankingPaymentStrategy implements PaymentStrategy {
    
    private static final BigDecimal FEE = new BigDecimal("10.00"); // Flat fee
    
    @Override
    public boolean processPayment(Payment payment) {
        if (!canProcess(payment.getAmount())) {
            payment.markFailed("Invalid amount");
            return false;
        }
        
        payment.markProcessing();
        
        try {
            Thread.sleep(150); // Net banking is typically slower
            
            // Simulate 90% success rate
            if (Math.random() > 0.10) {
                String transactionId = "NB-" + UUID.randomUUID().toString().substring(0, 10).toUpperCase();
                payment.markCompleted(transactionId);
                System.out.println("[PAYMENT] Net Banking payment processed: " + transactionId);
                return true;
            } else {
                payment.markFailed("Net Banking transaction failed - please retry");
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
            Thread.sleep(100);
            payment.markRefunded();
            System.out.println("[REFUND] Net Banking refund processed for: " + payment.getTransactionId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public BigDecimal getProcessingFee(BigDecimal amount) {
        return FEE;
    }
}



