package hotelmanagement.strategies.payment;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Online payment strategy (UPI, Net Banking, Digital Wallets)
 * In production, this would integrate with payment gateways like Razorpay, PayPal, etc.
 */
public class OnlinePaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        // Generate unique transaction reference
        String transactionRef = "ONLINE-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
        
        // Simulate online payment processing
        boolean success = simulateOnlinePayment(amount);
        
        Payment.Builder paymentBuilder = Payment.builder()
            .billId(bill.getId())
            .amount(amount)
            .method(PaymentMethod.ONLINE);
        
        if (success) {
            paymentBuilder
                .status(PaymentStatus.COMPLETED)
                .transactionReference(transactionRef);
            
            System.out.println("🌐 Online payment processed: $" + amount);
            System.out.println("   Transaction Ref: " + transactionRef);
        } else {
            paymentBuilder.status(PaymentStatus.FAILED);
            System.out.println("❌ Online payment failed");
        }
        
        return paymentBuilder.build();
    }
    
    private boolean simulateOnlinePayment(BigDecimal amount) {
        // Simulate processing delay
        try {
            Thread.sleep(200); // Online payments may take longer
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        
        // Simulate 92% success rate for online payments
        return Math.random() < 0.92;
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        // Online payments typically have a minimum amount
        BigDecimal minimumAmount = new BigDecimal("1.00");
        return amount != null && amount.compareTo(minimumAmount) >= 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Online Payment";
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        System.out.println("🌐 Online refund initiated for: " + payment.getTransactionReference());
        System.out.println("   Refund amount: $" + payment.getAmount());
        System.out.println("   Note: Refund may take 3-5 business days to process");
        
        String refundRef = "REFUND-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        System.out.println("   Refund Reference: " + refundRef);
        
        return true;
    }
}



