package bookmyshow.exceptions;

/**
 * Thrown when payment processing fails.
 */
public class PaymentFailedException extends BookMyShowException {
    
    public PaymentFailedException(String message) {
        super(message);
    }
    
    public PaymentFailedException(String message, Throwable cause) {
        super(message, cause);
    }
}



