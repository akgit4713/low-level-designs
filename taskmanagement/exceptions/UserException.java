package taskmanagement.exceptions;

/**
 * Exception for user-related errors.
 */
public class UserException extends RuntimeException {
    
    public UserException(String message) {
        super(message);
    }
    
    public UserException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static UserException notFound(String userId) {
        return new UserException("User not found with ID: " + userId);
    }
    
    public static UserException usernameExists(String username) {
        return new UserException("Username already exists: " + username);
    }
    
    public static UserException emailExists(String email) {
        return new UserException("Email already exists: " + email);
    }
    
    public static UserException invalidEmail(String email) {
        return new UserException("Invalid email format: " + email);
    }
}



