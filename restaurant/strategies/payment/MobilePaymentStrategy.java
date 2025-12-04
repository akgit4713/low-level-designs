package restaurant.strategies.payment;

import restaurant.enums.PaymentMethod;
import restaurant.models.Bill;
import restaurant.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Mobile payment strategy (UPI, Apple Pay, Google Pay, etc.)
 */
public class MobilePaymentStrategy implements PaymentStrategy {
    
    private final String providerName;
    
    public MobilePaymentStrategy() {
        this.providerName = "Mobile Payment";
    }
    
    public MobilePaymentStrategy(String providerName) {
        this.providerName = providerName;
    }
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        String paymentId = "PAY-MOB-" + UUID.randomUUID().toString().substring(0, 8);
        String transactionRef = generateUPIReference();
        
        Payment payment = new Payment(paymentId, bill, amount, PaymentMethod.MOBILE_PAYMENT, transactionRef);
        payment.markProcessing();
        
        // Simulate mobile payment verification
        if (verifyMobilePayment(transactionRef)) {
            payment.markCompleted();
        } else {
            payment.markFailed("Mobile payment verification failed");
        }
        
        return payment;
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        // Mobile payments typically have very high limits
        return amount != null && 
               amount.compareTo(BigDecimal.ZERO) > 0 && 
               amount.compareTo(new BigDecimal("100000.00")) <= 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return providerName;
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        if (!payment.isSuccessful()) {
            return false;
        }
        // Mobile refunds are processed back to the original payment source
        payment.markRefunded();
        return true;
    }
    
    private String generateUPIReference() {
        return "UPI" + System.currentTimeMillis() + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
    }
    
    private boolean verifyMobilePayment(String transactionRef) {
        // In production, would verify with payment provider
        return transactionRef != null && !transactionRef.isEmpty();
    }
    
    /**
     * Generate QR code data for mobile payment
     */
    public String generateQRCodeData(Bill bill) {
        return String.format("upi://pay?pa=restaurant@upi&pn=Restaurant&am=%s&tr=%s",
            bill.getTotalAmount(), bill.getId());
    }
}

