package onlineshopping.exceptions;

/**
 * Base exception for all shopping system exceptions
 */
public class ShoppingException extends RuntimeException {
    
    public ShoppingException(String message) {
        super(message);
    }

    public ShoppingException(String message, Throwable cause) {
        super(message, cause);
    }
}



