package cricinfo.exceptions;

import cricinfo.enums.MatchStatus;

/**
 * Exception thrown when an operation is invalid for the current match state.
 */
public class InvalidMatchStateException extends CricInfoException {
    
    public InvalidMatchStateException(MatchStatus currentStatus, String operation) {
        super("Cannot perform '" + operation + "' when match is in " + currentStatus + " state");
    }
    
    public InvalidMatchStateException(String message) {
        super(message);
    }
}



