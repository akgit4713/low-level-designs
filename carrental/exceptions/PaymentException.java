package carrental.exceptions;

/**
 * Thrown when a payment operation fails.
 */
public class PaymentException extends CarRentalException {
    
    public PaymentException(String message) {
        super(message);
    }

    public PaymentException(String message, Throwable cause) {
        super(message, cause);
    }
}



