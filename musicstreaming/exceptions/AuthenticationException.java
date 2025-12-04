package musicstreaming.exceptions;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends MusicStreamingException {
    
    public AuthenticationException(String message) {
        super(message);
    }
    
    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}



