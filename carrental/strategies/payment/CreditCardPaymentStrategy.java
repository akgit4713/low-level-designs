package carrental.strategies.payment;

import carrental.models.Payment;

import java.util.UUID;

/**
 * Credit card payment strategy implementation.
 * In production, this would integrate with a payment gateway.
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Simulate payment processing
        payment.markAsProcessing();
        
        // In real implementation, would call payment gateway API
        // Simulating successful payment
        boolean success = simulatePaymentGatewayCall();
        
        if (success) {
            String transactionRef = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
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
        
        // Simulate refund processing
        boolean success = simulateRefundGatewayCall();
        
        if (success) {
            payment.markAsRefunded();
            return true;
        }
        return false;
    }

    private boolean simulatePaymentGatewayCall() {
        // Simulate 95% success rate
        return Math.random() > 0.05;
    }

    private boolean simulateRefundGatewayCall() {
        // Simulate 98% success rate for refunds
        return Math.random() > 0.02;
    }

    @Override
    public String getStrategyName() {
        return "Credit Card Payment";
    }
}



