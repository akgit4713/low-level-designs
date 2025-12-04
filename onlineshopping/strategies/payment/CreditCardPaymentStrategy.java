package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.util.UUID;

/**
 * Payment strategy for credit card payments
 */
public class CreditCardPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Order order, Payment payment) {
        try {
            payment.startProcessing();
            
            // Simulate credit card processing
            simulatePaymentGatewayCall();
            
            // Generate transaction ID
            String transactionId = "CC-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.complete(transactionId);
            
            System.out.printf("[CREDIT CARD] Payment of $%s processed successfully. Transaction: %s%n",
                order.getTotalAmount(), transactionId);
            
            return true;
        } catch (Exception e) {
            payment.fail("Credit card processing failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validate(Order order) {
        // In a real implementation, this would validate card details
        return order.getTotalAmount().doubleValue() > 0;
    }

    @Override
    public boolean processRefund(Payment payment) {
        try {
            // Simulate refund processing
            simulatePaymentGatewayCall();
            payment.refund(payment.getAmount());
            
            System.out.printf("[CREDIT CARD] Refund of $%s processed for transaction: %s%n",
                payment.getAmount(), payment.getTransactionId().orElse("N/A"));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void simulatePaymentGatewayCall() {
        // Simulate network delay
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



