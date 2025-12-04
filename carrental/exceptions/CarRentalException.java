package carrental.exceptions;

/**
 * Base exception for all car rental system exceptions.
 */
public class CarRentalException extends RuntimeException {
    
    public CarRentalException(String message) {
        super(message);
    }

    public CarRentalException(String message, Throwable cause) {
        super(message, cause);
    }
}



