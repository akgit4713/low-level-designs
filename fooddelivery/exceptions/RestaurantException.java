package fooddelivery.exceptions;

public class RestaurantException extends FoodDeliveryException {
    
    public RestaurantException(String message) {
        super(message);
    }
    
    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
    }
}



