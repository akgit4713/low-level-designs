package restaurant.strategies.payment;

import restaurant.enums.PaymentMethod;
import restaurant.exceptions.PaymentException;
import restaurant.models.Bill;
import restaurant.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Credit/Debit card payment strategy implementation
 */
public class CardPaymentStrategy implements PaymentStrategy {
    
    private final PaymentMethod cardType;
    private final BigDecimal minimumAmount;
    private final BigDecimal maximumAmount;
    
    public CardPaymentStrategy(PaymentMethod cardType) {
        this.cardType = cardType;
        this.minimumAmount = new BigDecimal("1.00");
        this.maximumAmount = new BigDecimal("50000.00");
    }
    
    public CardPaymentStrategy(PaymentMethod cardType, BigDecimal minimumAmount, BigDecimal maximumAmount) {
        this.cardType = cardType;
        this.minimumAmount = minimumAmount;
        this.maximumAmount = maximumAmount;
    }
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        String paymentId = "PAY-CARD-" + UUID.randomUUID().toString().substring(0, 8);
        String transactionRef = generateTransactionReference();
        
        Payment payment = new Payment(paymentId, bill, amount, cardType, transactionRef);
        payment.markProcessing();
        
        // Simulate card processing
        try {
            validateCardPayment(amount);
            // In real implementation, this would call payment gateway
            simulatePaymentGateway();
            payment.markCompleted();
        } catch (PaymentException e) {
            payment.markFailed(e.getMessage());
        }
        
        return payment;
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        if (amount == null) return false;
        return amount.compareTo(minimumAmount) >= 0 && amount.compareTo(maximumAmount) <= 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return cardType.getDisplayName();
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        if (!payment.isSuccessful()) {
            return false;
        }
        
        // In real implementation, would call payment gateway for refund
        payment.markRefunded();
        return true;
    }
    
    private void validateCardPayment(BigDecimal amount) {
        if (amount.compareTo(minimumAmount) < 0) {
            throw PaymentException.paymentFailed(
                "Minimum card payment is " + minimumAmount
            );
        }
        if (amount.compareTo(maximumAmount) > 0) {
            throw PaymentException.paymentFailed(
                "Maximum card payment is " + maximumAmount
            );
        }
    }
    
    private String generateTransactionReference() {
        return "TXN-" + System.currentTimeMillis() + "-" + UUID.randomUUID().toString().substring(0, 4);
    }
    
    private void simulatePaymentGateway() {
        // Simulate network delay
        // In production, this would be actual gateway integration
    }
}

