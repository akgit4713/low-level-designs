package musicstreaming.exceptions;

/**
 * Base exception for all music streaming related errors.
 */
public class MusicStreamingException extends RuntimeException {
    
    public MusicStreamingException(String message) {
        super(message);
    }
    
    public MusicStreamingException(String message, Throwable cause) {
        super(message, cause);
    }
}



