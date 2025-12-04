package cricinfo.exceptions;

/**
 * Base exception for all CricInfo system exceptions.
 */
public class CricInfoException extends RuntimeException {
    
    public CricInfoException(String message) {
        super(message);
    }
    
    public CricInfoException(String message, Throwable cause) {
        super(message, cause);
    }
}



