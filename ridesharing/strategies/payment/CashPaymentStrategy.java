package ridesharing.strategies.payment;

import ridesharing.enums.PaymentMethod;
import ridesharing.enums.PaymentStatus;
import ridesharing.models.Payment;

import java.util.UUID;

/**
 * Payment strategy for cash payments.
 * Cash payments are marked as pending until driver confirms receipt.
 */
public class CashPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Payment payment) {
        // Cash payments are "processed" when the ride completes
        // The actual cash exchange happens between passenger and driver
        payment.setStatus(PaymentStatus.COMPLETED);
        payment.setTransactionReference("CASH-" + UUID.randomUUID().toString().substring(0, 8));
        return true;
    }

    @Override
    public boolean refundPayment(Payment payment) {
        // Cash refunds are handled outside the system
        payment.setStatus(PaymentStatus.REFUNDED);
        return true;
    }

    @Override
    public boolean canHandle(Payment payment) {
        return payment.getPaymentMethod() == PaymentMethod.CASH;
    }

    @Override
    public String getStrategyName() {
        return "Cash Payment";
    }
}



