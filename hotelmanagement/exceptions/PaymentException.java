package hotelmanagement.exceptions;

import hotelmanagement.enums.PaymentMethod;

/**
 * Exception class for payment-related errors
 */
public class PaymentException extends HotelException {
    
    public PaymentException(String message) {
        super(message);
    }
    
    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static PaymentException paymentFailed(String reason) {
        return new PaymentException("Payment failed: " + reason);
    }
    
    public static PaymentException insufficientAmount(String billId, String expected, String provided) {
        return new PaymentException(String.format(
            "Insufficient payment amount for bill %s: expected %s, provided %s",
            billId, expected, provided
        ));
    }
    
    public static PaymentException unsupportedPaymentMethod(PaymentMethod method) {
        return new PaymentException("Unsupported payment method: " + method);
    }
    
    public static PaymentException billNotFound(String billId) {
        return new PaymentException("Bill not found: " + billId);
    }
    
    public static PaymentException billAlreadyPaid(String billId) {
        return new PaymentException("Bill already paid: " + billId);
    }
    
    public static PaymentException refundFailed(String paymentId, String reason) {
        return new PaymentException(String.format(
            "Refund failed for payment %s: %s",
            paymentId, reason
        ));
    }
}



