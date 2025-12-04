package restaurant.exceptions;

/**
 * Exception for payment-related errors
 */
public class PaymentException extends RestaurantException {

    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }

    public static PaymentException paymentFailed(String reason) {
        return new PaymentException("Payment failed: " + reason);
    }

    public static PaymentException insufficientAmount(double required, double provided) {
        return new PaymentException(
            String.format("Insufficient payment: required %.2f, provided %.2f", required, provided)
        );
    }

    public static PaymentException invalidPaymentMethod(String method) {
        return new PaymentException("Invalid payment method: " + method);
    }
}

