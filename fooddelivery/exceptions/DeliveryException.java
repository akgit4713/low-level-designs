package fooddelivery.exceptions;

public class DeliveryException extends FoodDeliveryException {
    
    public DeliveryException(String message) {
        super(message);
    }
    
    public DeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}



