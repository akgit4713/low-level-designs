package cricinfo.exceptions;

/**
 * Exception thrown when a match is not found.
 */
public class MatchNotFoundException extends CricInfoException {
    
    public MatchNotFoundException(String matchId) {
        super("Match not found with ID: " + matchId);
    }
    
    public MatchNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



