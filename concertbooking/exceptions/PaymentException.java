package concertbooking.exceptions;

import concertbooking.enums.PaymentMethod;

/**
 * Exception for payment-related errors
 */
public class PaymentException extends ConcertBookingException {
    
    public PaymentException(String message) {
        super(message);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PaymentException failed(String reason) {
        return new PaymentException("Payment failed: " + reason);
    }
    
    public static PaymentException invalidMethod(PaymentMethod method) {
        return new PaymentException("Payment method not supported: " + method.getDisplayName());
    }
    
    public static PaymentException insufficientAmount(double expected, double actual) {
        return new PaymentException(
            String.format("Insufficient payment amount. Expected: %.2f, Received: %.2f", expected, actual)
        );
    }
    
    public static PaymentException refundFailed(String reason) {
        return new PaymentException("Refund failed: " + reason);
    }
    
    public static PaymentException alreadyProcessed(String paymentId) {
        return new PaymentException("Payment already processed: " + paymentId);
    }
}



