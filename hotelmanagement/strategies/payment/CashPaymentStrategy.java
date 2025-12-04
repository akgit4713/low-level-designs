package hotelmanagement.strategies.payment;

import hotelmanagement.enums.PaymentMethod;
import hotelmanagement.enums.PaymentStatus;
import hotelmanagement.models.Bill;
import hotelmanagement.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cash payment strategy implementation
 */
public class CashPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        // Cash payments are always successful (assuming correct amount received)
        String transactionRef = "CASH-" + UUID.randomUUID().toString().substring(0, 8);
        
        Payment payment = Payment.builder()
            .billId(bill.getId())
            .amount(amount)
            .method(PaymentMethod.CASH)
            .status(PaymentStatus.COMPLETED)
            .transactionReference(transactionRef)
            .build();
        
        System.out.println("💵 Cash payment received: $" + amount);
        System.out.println("   Transaction Ref: " + transactionRef);
        
        return payment;
    }
    
    @Override
    public boolean canProcess(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
    
    @Override
    public String getPaymentMethodName() {
        return "Cash";
    }
    
    @Override
    public boolean processRefund(Payment payment) {
        // Cash refunds require manual processing
        System.out.println("💵 Cash refund initiated for payment: " + payment.getId());
        System.out.println("   Please provide cash refund of: $" + payment.getAmount());
        return true;
    }
}



