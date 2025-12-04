package onlineshopping.exceptions;

import onlineshopping.enums.PaymentMethod;

/**
 * Exception for payment-related errors
 */
public class PaymentException extends ShoppingException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PaymentException failed(String reason) {
        return new PaymentException("Payment failed: " + reason);
    }

    public static PaymentException unsupportedMethod(PaymentMethod method) {
        return new PaymentException("Unsupported payment method: " + method);
    }

    public static PaymentException insufficientFunds() {
        return new PaymentException("Insufficient funds for payment");
    }

    public static PaymentException invalidCard() {
        return new PaymentException("Invalid card details");
    }

    public static PaymentException refundFailed(String paymentId, String reason) {
        return new PaymentException(
            String.format("Refund failed for payment %s: %s", paymentId, reason)
        );
    }
}



