package hotelmanagement.exceptions;

/**
 * Exception class for billing-related errors
 */
public class BillingException extends HotelException {
    
    public BillingException(String message) {
        super(message);
    }
    
    public BillingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static BillingException billNotFound(String billId) {
        return new BillingException("Bill not found: " + billId);
    }
    
    public static BillingException billAlreadyGenerated(String reservationId) {
        return new BillingException("Bill already generated for reservation: " + reservationId);
    }
    
    public static BillingException cannotGenerateBill(String reservationId, String reason) {
        return new BillingException(String.format(
            "Cannot generate bill for reservation %s: %s",
            reservationId, reason
        ));
    }
    
    public static BillingException invalidAmount(String reason) {
        return new BillingException("Invalid amount: " + reason);
    }
}



