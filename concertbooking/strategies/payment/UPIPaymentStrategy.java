package concertbooking.strategies.payment;

import concertbooking.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment strategy for UPI payments
 */
public class UPIPaymentStrategy implements PaymentStrategy {
    
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("200000.00");
    
    @Override
    public boolean processPayment(Payment payment) {
        if (!canProcess(payment.getAmount())) {
            payment.markFailed("Amount exceeds UPI limit");
            return false;
        }
        
        payment.markProcessing();
        
        // Simulate UPI payment
        try {
            Thread.sleep(80);
            
            // Simulate 98% success rate for UPI
            if (Math.random() > 0.02) {
                String transactionId = "UPI-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
                payment.markCompleted(transactionId);
                System.out.println("[PAYMENT] UPI payment processed: " + transactionId);
                return true;
            } else {
                payment.markFailed("UPI transaction failed");
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
            Thread.sleep(50);
            payment.markRefunded();
            System.out.println("[REFUND] UPI refund processed for: " + payment.getTransactionId());
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
        return BigDecimal.ZERO; // UPI typically has no fees for consumers
    }
}



