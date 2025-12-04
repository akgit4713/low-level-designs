package musicstreaming.exceptions;

/**
 * Exception thrown when playback operations fail.
 */
public class PlaybackException extends MusicStreamingException {
    
    public PlaybackException(String message) {
        super(message);
    }
    
    public PlaybackException(String message, Throwable cause) {
        super(message, cause);
    }
}



