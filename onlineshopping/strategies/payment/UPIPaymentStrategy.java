package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.util.UUID;

/**
 * Payment strategy for UPI payments
 */
public class UPIPaymentStrategy implements PaymentStrategy {

    @Override
    public boolean processPayment(Order order, Payment payment) {
        try {
            payment.startProcessing();
            
            // Simulate UPI payment
            simulateUPICall();
            
            // Generate transaction ID
            String transactionId = "UPI-" + UUID.randomUUID().toString().substring(0, 12).toUpperCase();
            payment.complete(transactionId);
            
            System.out.printf("[UPI] Payment of $%s processed successfully. Transaction: %s%n",
                order.getTotalAmount(), transactionId);
            
            return true;
        } catch (Exception e) {
            payment.fail("UPI payment failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validate(Order order) {
        // UPI typically has transaction limits
        return order.getTotalAmount().doubleValue() > 0 && 
               order.getTotalAmount().doubleValue() <= 100000;
    }

    @Override
    public boolean processRefund(Payment payment) {
        try {
            simulateUPICall();
            payment.refund(payment.getAmount());
            
            System.out.printf("[UPI] Refund of $%s processed for transaction: %s%n",
                payment.getAmount(), payment.getTransactionId().orElse("N/A"));
            
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private void simulateUPICall() {
        try {
            Thread.sleep(50);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}



