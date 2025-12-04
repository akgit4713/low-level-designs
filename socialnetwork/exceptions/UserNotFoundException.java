package socialnetwork.exceptions;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends SocialNetworkException {
    
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }

    public static UserNotFoundException withEmail(String email) {
        return new UserNotFoundException("User not found with email: " + email);
    }
}



