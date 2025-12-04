package carrental.strategies.payment;

import carrental.models.Payment;

import java.util.UUID;

/**
 * Debit card payment strategy implementation.
 */
public class DebitCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        payment.markAsProcessing();
        
        // Simulate debit card processing (instant verification)
        boolean success = simulateDebitPayment();
        
        if (success) {
            String transactionRef = "DC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.markAsCompleted(transactionRef);
            return true;
        } else {
            payment.markAsFailed();
            return false;
        }
    }

    @Override
    public boolean processRefund(Payment payment) {
        if (!payment.isSuccessful()) {
            return false;
        }
        
        boolean success = simulateDebitRefund();
        if (success) {
            payment.markAsRefunded();
            return true;
        }
        return false;
    }

    private boolean simulateDebitPayment() {
        return Math.random() > 0.03; // 97% success rate
    }

    private boolean simulateDebitRefund() {
        return Math.random() > 0.01; // 99% success rate
    }

    @Override
    public String getStrategyName() {
        return "Debit Card Payment";
    }
}



