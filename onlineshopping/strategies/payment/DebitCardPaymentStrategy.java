package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.util.UUID;

/**
 * Payment strategy for debit card payments
 */
public class DebitCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Order order, Payment payment) {
        try {
            payment.startProcessing();
            
            // Simulate debit card processing
            simulatePaymentGatewayCall();
            
            // Generate transaction ID
            String transactionId = "DC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.complete(transactionId);
            
            System.out.printf("[DEBIT CARD] Payment of $%s processed successfully. Transaction: %s%n",
                order.getTotalAmount(), transactionId);
            
            return true;
        } catch (Exception e) {
            payment.fail("Debit card processing failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validate(Order order) {
        return order.getTotalAmount().doubleValue() > 0;
    }

    @Override
    public boolean processRefund(Payment payment) {
        try {
            simulatePaymentGatewayCall();
            payment.refund(payment.getAmount());
            
            System.out.printf("[DEBIT CARD] Refund of $%s processed for transaction: %s%n",
                payment.getAmount(), payment.getTransactionId().orElse("N/A"));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void simulatePaymentGatewayCall() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



