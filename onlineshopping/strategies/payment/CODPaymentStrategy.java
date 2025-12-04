package onlineshopping.strategies.payment;

import onlineshopping.models.Order;
import onlineshopping.models.Payment;

import java.math.BigDecimal;
import java.util.UUID;

/**
 * Payment strategy for Cash on Delivery
 */
public class CODPaymentStrategy implements PaymentStrategy {
    
    private static final BigDecimal MAX_COD_AMOUNT = new BigDecimal("5000");
    private static final BigDecimal COD_FEE = new BigDecimal("49");

    @Override
    public boolean processPayment(Order order, Payment payment) {
        try {
            // COD payment is marked as pending until delivery
            String transactionId = "COD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            payment.complete(transactionId);
            
            System.out.printf("[COD] Cash on Delivery order placed. Amount to collect: $%s%n",
                order.getTotalAmount());
            
            return true;
        } catch (Exception e) {
            payment.fail("COD setup failed: " + e.getMessage());
            return false;
        }
    }

    @Override
    public boolean validate(Order order) {
        // COD has amount limits
        return order.getTotalAmount().compareTo(MAX_COD_AMOUNT) <= 0;
    }

    @Override
    public boolean processRefund(Payment payment) {
        // COD refunds are typically handled differently
        System.out.printf("[COD] Refund of $%s will be processed via bank transfer%n", 
            payment.getAmount());
        payment.refund(payment.getAmount());
        return true;
    }

    /**
     * Get COD fee
     */
    public static BigDecimal getCodFee() {
        return COD_FEE;
    }

    /**
     * Get maximum COD amount
     */
    public static BigDecimal getMaxCodAmount() {
        return MAX_COD_AMOUNT;
    }
}



