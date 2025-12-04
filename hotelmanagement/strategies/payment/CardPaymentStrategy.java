package hotelmanagement.strategies.payment;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Credit/Debit card payment strategy implementation
 * In production, this would integrate with a payment gateway (Stripe, Square, etc.)
 */
public class CardPaymentStrategy implements PaymentStrategy {
    
    private final PaymentMethod cardType;
    
    public CardPaymentStrategy(PaymentMethod cardType) {
        if (cardType != PaymentMethod.CREDIT_CARD && cardType != PaymentMethod.DEBIT_CARD) {
            throw new IllegalArgumentException("Card type must be CREDIT_CARD or DEBIT_CARD");
        }
        this.cardType = cardType;
    }
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        // Simulate card processing
        String transactionRef = "CARD-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        
        // Simulate successful payment (in production, this would call a payment gateway)
        boolean success = simulateCardProcessing(amount);
        
        Payment.Builder paymentBuilder = Payment.builder()
            .billId(bill.getId())
            .amount(amount)
            .method(cardType);
        
        if (success) {
            paymentBuilder
                .status(PaymentStatus.COMPLETED)
                .transactionReference(transactionRef);
            
            System.out.println("💳 " + cardType.getDisplayName() + " payment processed: $" + amount);
            System.out.println("   Transaction Ref: " + transactionRef);
        } else {
            paymentBuilder.status(PaymentStatus.FAILED);
            System.out.println("❌ " + cardType.getDisplayName() + " payment failed");
        }
        
        return paymentBuilder.build();
    }
    
    private boolean simulateCardProcessing(BigDecimal amount) {
        // Simulate processing delay
        try {
            Thread.sleep(100); // Simulate network delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 95% success rate
        return Math.random() < 0.95;
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        // Minimum transaction amount for cards
        BigDecimal minimumAmount = new BigDecimal("0.50");
        return amount != null && amount.compareTo(minimumAmount) >= 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return cardType.getDisplayName();
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("💳 Card refund initiated for: " + payment.getTransactionReference());
        System.out.println("   Refund amount: $" + payment.getAmount());
        
        // Simulate refund processing
        String refundRef = "REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("   Refund Reference: " + refundRef);
        
        return true;
    }
}



