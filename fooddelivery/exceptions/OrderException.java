package fooddelivery.exceptions;

public class OrderException extends FoodDeliveryException {
    
    public OrderException(String message) {
        super(message);
    }
    
    public OrderException(String message, Throwable cause) {
        super(message, cause);
    }
}



