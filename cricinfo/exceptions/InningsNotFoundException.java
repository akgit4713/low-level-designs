package cricinfo.exceptions;

/**
 * Exception thrown when an innings is not found.
 */
public class InningsNotFoundException extends CricInfoException {
    
    public InningsNotFoundException(String matchId, int inningsNumber) {
        super("Innings " + inningsNumber + " not found in match: " + matchId);
    }
    
    public InningsNotFoundException(String message) {
        super(message);
    }
}



