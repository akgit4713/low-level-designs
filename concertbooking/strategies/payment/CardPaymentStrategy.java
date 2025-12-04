package concertbooking.strategies.payment;

import concertbooking.enums.PaymentMethod;
import concertbooking.models.Payment;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.UUID;

/**
 * Payment strategy for credit/debit card payments
 */
public class CardPaymentStrategy implements PaymentStrategy {
    
    private static final BigDecimal FEE_PERCENTAGE = new BigDecimal("0.025"); // 2.5%
    private static final BigDecimal MIN_AMOUNT = new BigDecimal("1.00");
    private static final BigDecimal MAX_AMOUNT = new BigDecimal("100000.00");
    
    private final PaymentMethod cardType;
    
    public CardPaymentStrategy(PaymentMethod cardType) {
        if (cardType != PaymentMethod.CREDIT_CARD && cardType != PaymentMethod.DEBIT_CARD) {
            throw new IllegalArgumentException("Card type must be CREDIT_CARD or DEBIT_CARD");
        }
        this.cardType = cardType;
    }
    
    @Override
    public boolean processPayment(Payment payment) {
        if (!canProcess(payment.getAmount())) {
            payment.markFailed("Amount out of acceptable range");
            return false;
        }
        
        payment.markProcessing();
        
        // Simulate payment gateway call
        try {
            Thread.sleep(100); // Simulate network latency
            
            // Simulate 95% success rate
            if (Math.random() > 0.05) {
                String transactionId = "TXN-" + cardType.name().substring(0, 2) 
                    + "-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
                payment.markCompleted(transactionId);
                System.out.println("[PAYMENT] " + cardType.getDisplayName() 
                    + " payment processed: " + transactionId);
                return true;
            } else {
                payment.markFailed("Card declined by issuing bank");
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
        
        // Simulate refund processing
        try {
            Thread.sleep(50);
            payment.markRefunded();
            System.out.println("[REFUND] " + cardType.getDisplayName() 
                + " refund processed for: " + payment.getTransactionId());
            return true;
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return false;
        }
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount.compareTo(MIN_AMOUNT) >= 0 && amount.compareTo(MAX_AMOUNT) <= 0;
    }
    
    @Override
    public BigDecimal getProcessingFee(BigDecimal amount) {
        return amount.multiply(FEE_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
    }
}



