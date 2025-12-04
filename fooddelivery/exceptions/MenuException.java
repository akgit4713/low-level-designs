package fooddelivery.exceptions;

public class MenuException extends FoodDeliveryException {
    
    public MenuException(String message) {
        super(message);
    }
    
    public MenuException(String message, Throwable cause) {
        super(message, cause);
    }
}



