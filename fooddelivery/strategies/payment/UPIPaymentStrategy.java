package fooddelivery.strategies.payment;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import java.util.UUID;

/**
 * UPI payment processing strategy.
 */
public class UPIPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            payment.markFailed("Invalid payment details");
            return false;
        }
        
        // Simulate UPI payment
        try {
            Thread.sleep(50); // UPI is usually faster
            
            // Simulate 98% success rate for UPI
            if (Math.random() < 0.98) {
                String transactionId = "UPI-" + UUID.randomUUID().toString().substring(0, 12);
                payment.markCompleted(transactionId);
                System.out.println("[UPI] Payment successful: " + transactionId);
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
    public boolean refundPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        System.out.println("[UPI] Refund initiated for: " + payment.getTransactionId());
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



