package splitwise.exceptions;

/**
 * Exception thrown when a requested user is not found.
 */
public class UserNotFoundException extends SplitwiseException {
    
    public UserNotFoundException(String userId) {
        super("User not found with ID: " + userId);
    }
}



