package fooddelivery.strategies.payment;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import java.util.UUID;

/**
 * Credit/Debit card payment processing strategy.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            payment.markFailed("Invalid payment details");
            return false;
        }
        
        // Simulate payment gateway call
        try {
            Thread.sleep(100); // Simulate network latency
            
            // Simulate 95% success rate
            if (Math.random() < 0.95) {
                String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8);
                payment.markCompleted(transactionId);
                System.out.println("[CreditCard] Payment successful: " + transactionId);
                return true;
            } else {
                payment.markFailed("Payment declined by bank");
                return false;
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            payment.markFailed("Payment processing interrupted");
            return false;
        }
    }

    @Override
    public boolean refundPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        // Simulate refund processing
        System.out.println("[CreditCard] Refund initiated for: " + payment.getTransactionId());
        payment.setStatus(PaymentStatus.REFUNDED);
        return true;
    }

    @Override
    public boolean validatePaymentDetails(Payment payment) {
        return payment != null && 
               payment.getAmount() != null && 
               payment.getAmount().doubleValue() > 0;
    }
}



