package bookmyshow.exceptions;

/**
 * Base exception for BookMyShow system.
 */
public class BookMyShowException extends RuntimeException {
    
    public BookMyShowException(String message) {
        super(message);
    }
    
    public BookMyShowException(String message, Throwable cause) {
        super(message, cause);
    }
}



