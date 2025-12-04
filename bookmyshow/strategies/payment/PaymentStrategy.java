package bookmyshow.strategies.payment;

import bookmyshow.models.Payment;

/**
 * Strategy interface for processing payments.
 * Different implementations handle different payment methods.
 */
public interface PaymentStrategy {
    
    /**
     * Process the payment.
     * @param payment Payment object with amount and details
     * @return true if payment was successful
     */
    boolean processPayment(Payment payment);
    
    /**
     * Process a refund for a payment.
     * @param payment Original payment to refund
     * @return true if refund was successful
     */
    boolean processRefund(Payment payment);
    
    /**
     * Get the name of this payment method.
     * @return Payment method name
     */
    String getPaymentMethodName();
}



