package fooddelivery.exceptions;

/**
 * Base exception for all food delivery domain exceptions.
 */
public class FoodDeliveryException extends RuntimeException {
    
    public FoodDeliveryException(String message) {
        super(message);
    }
    
    public FoodDeliveryException(String message, Throwable cause) {
        super(message, cause);
    }
}



