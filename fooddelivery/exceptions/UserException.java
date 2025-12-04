package fooddelivery.exceptions;

public class UserException extends FoodDeliveryException {
    
    public UserException(String message) {
        super(message);
    }
    
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
}



