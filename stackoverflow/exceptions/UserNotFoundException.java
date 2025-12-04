package stackoverflow.exceptions;

/**
 * Exception thrown when a user is not found.
 */
public class UserNotFoundException extends StackOverflowException {
    public UserNotFoundException(String userId) {
        super("User not found: " + userId);
    }
}



