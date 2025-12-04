package cricinfo.exceptions;

/**
 * Exception thrown when a score update is invalid.
 */
public class InvalidScoreUpdateException extends CricInfoException {
    
    public InvalidScoreUpdateException(String message) {
        super(message);
    }
    
    public InvalidScoreUpdateException(String message, Throwable cause) {
        super(message, cause);
    }
}



