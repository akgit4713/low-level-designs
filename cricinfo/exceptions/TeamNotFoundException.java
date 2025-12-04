package cricinfo.exceptions;

/**
 * Exception thrown when a team is not found.
 */
public class TeamNotFoundException extends CricInfoException {
    
    public TeamNotFoundException(String teamId) {
        super("Team not found with ID: " + teamId);
    }
    
    public TeamNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}



