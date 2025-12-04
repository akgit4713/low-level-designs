package fooddelivery.strategies.payment;

import fooddelivery.enums.PaymentStatus;
import fooddelivery.models.Payment;
import java.util.UUID;

/**
 * Cash on Delivery payment strategy.
 */
public class CashOnDeliveryStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        if (!validatePaymentDetails(payment)) {
            payment.markFailed("Invalid payment details");
            return false;
        }
        
        // COD is always "successful" at order time - actual collection happens at delivery
        String transactionId = "COD-" + UUID.randomUUID().toString().substring(0, 8);
        payment.markCompleted(transactionId);
        System.out.println("[COD] Payment recorded (to collect): " + transactionId);
        return true;
    }

    @Override
    public boolean refundPayment(Payment payment) {
        // COD refunds are handled differently (no money collected yet if not delivered)
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        System.out.println("[COD] Order cancelled, no refund needed: " + payment.getTransactionId());
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



