package restaurant.exceptions;

/**
 * Base exception for all restaurant system exceptions
 */
public class RestaurantException extends RuntimeException {
    
    public RestaurantException(String message) {
        super(message);
    }

    public RestaurantException(String message, Throwable cause) {
        super(message, cause);
    }
}

