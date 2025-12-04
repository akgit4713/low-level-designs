package musicstreaming.exceptions;

/**
 * Exception thrown when a requested user is not found.
 */
public class UserNotFoundException extends MusicStreamingException {
    
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}



