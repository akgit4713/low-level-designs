package socialnetwork.exceptions;

/**
 * Base exception for all social network related exceptions.
 */
public class SocialNetworkException extends RuntimeException {
    
    public SocialNetworkException(String message) {
        super(message);
    }

    public SocialNetworkException(String message, Throwable cause) {
        super(message, cause);
    }
}



