package musicstreaming.exceptions;

/**
 * Exception thrown when a user attempts an unauthorized action.
 */
public class UnauthorizedException extends MusicStreamingException {
    
    public UnauthorizedException(String message) {
        super(message);
    }
}



