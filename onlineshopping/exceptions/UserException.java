package onlineshopping.exceptions;

/**
 * Exception for user-related errors
 */
public class UserException extends ShoppingException {

    public UserException(String message) {
        super(message);
    }

    public static UserException notFound(String userId) {
        return new UserException("User not found: " + userId);
    }

    public static UserException emailAlreadyExists(String email) {
        return new UserException("Email already registered: " + email);
    }

    public static UserException invalidCredentials() {
        return new UserException("Invalid email or password");
    }

    public static UserException unauthorized(String action) {
        return new UserException("Unauthorized to perform action: " + action);
    }

    public static UserException invalidAddress(String addressId) {
        return new UserException("Invalid address: " + addressId);
    }
}



