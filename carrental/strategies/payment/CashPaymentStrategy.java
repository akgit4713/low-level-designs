package carrental.strategies.payment;

import carrental.models.Payment;

import java.util.UUID;

/**
 * Cash payment strategy implementation.
 * For in-person payments at pickup.
 */
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        payment.markAsProcessing();
        
        // Cash payments are always successful when processed
        // The validation happens at pickup
        String transactionRef = "CASH-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        payment.markAsCompleted(transactionRef);
        return true;
    }

    @Override
    public boolean processRefund(Payment payment) {
        if (!payment.isSuccessful()) {
            return false;
        }
        
        // Cash refunds are handled manually
        payment.markAsRefunded();
        return true;
    }

    @Override
    public String getStrategyName() {
        return "Cash Payment";
    }
}



