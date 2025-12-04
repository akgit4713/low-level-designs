package restaurant.strategies.payment;

import restaurant.enums.PaymentMethod;
import restaurant.models.Bill;
import restaurant.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Cash payment strategy implementation
 */
public class CashPaymentStrategy implements PaymentStrategy {
    
    @Override
    public Payment processPayment(Bill bill, BigDecimal amount) {
        String paymentId = "PAY-CASH-" + UUID.randomUUID().toString().substring(0, 8);
        Payment payment = new Payment(paymentId, bill, amount, PaymentMethod.CASH, null);
        
        payment.markProcessing();
        
        // Cash payment is always successful if amount is sufficient
        if (amount.compareTo(bill.getTotalAmount()) >= 0) {
            payment.markCompleted();
        } else {
            payment.markFailed("Insufficient cash provided");
        }
        
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
        if (payment.isSuccessful()) {
            payment.markRefunded();
            return true;
        }
        return false;
    }
    
    /**
     * Calculate change to return
     */
    public BigDecimal calculateChange(BigDecimal totalAmount, BigDecimal cashProvided) {
        BigDecimal change = cashProvided.subtract(totalAmount);
        return change.compareTo(BigDecimal.ZERO) > 0 ? change : BigDecimal.ZERO;
    }
}

