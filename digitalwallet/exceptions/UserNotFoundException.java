package digitalwallet.exceptions;

/**
 * Exception thrown when a user is not found in the system.
 */
public class UserNotFoundException extends WalletException {
    
    private final String userId;
    private final String email;

    public UserNotFoundException(String userId) {
        super(String.format("User not found with ID: %s", userId), "USER_NOT_FOUND");
        this.userId = userId;
        this.email = null;
    }

    public static UserNotFoundException byEmail(String email) {
        UserNotFoundException ex = new UserNotFoundException(
            String.format("User not found with email: %s", email), null, email);
        return ex;
    }

    private UserNotFoundException(String message, String userId, String email) {
        super(message, "USER_NOT_FOUND");
        this.userId = userId;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getEmail() {
        return email;
    }
}



