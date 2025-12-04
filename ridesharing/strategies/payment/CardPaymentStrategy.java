package ridesharing.strategies.payment;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.PaymentStatus;
import ridesharing.models.Payment;

import java.util.UUID;

/**
 * Payment strategy for credit/debit card payments.
 * In production, this would integrate with payment gateways like Stripe.
 */
public class CardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Simulate payment processing
        try {
            // In production: call payment gateway API
            Thread.sleep(100); // Simulate network latency
            
            // Simulate success (in production, check gateway response)
            payment.setStatus(PaymentStatus.COMPLETED);
            payment.setTransactionReference("TXN-" + UUID.randomUUID().toString().substring(0, 8));
            return true;
        } catch (Exception e) {
            payment.setStatus(PaymentStatus.FAILED);
            return false;
        }
    }

    @Override
    public boolean refundPayment(Payment payment) {
        if (payment.getStatus() != PaymentStatus.COMPLETED) {
            return false;
        }
        
        try {
            // In production: call payment gateway refund API
            Thread.sleep(100);
            payment.setStatus(PaymentStatus.REFUNDED);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean canHandle(Payment payment) {
        PaymentMethod method = payment.getPaymentMethod();
        return method == PaymentMethod.CREDIT_CARD || method == PaymentMethod.DEBIT_CARD;
    }

    @Override
    public String getStrategyName() {
        return "Card Payment";
    }
}



