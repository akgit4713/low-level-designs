package digitalwallet.exceptions;

/**
 * Exception thrown when authentication or authorization fails.
 */
public class AuthenticationException extends WalletException {
    
    private final String userId;
    private final AuthFailureReason reason;

    public enum AuthFailureReason {
        INVALID_PIN("Invalid PIN"),
        INVALID_PASSWORD("Invalid password"),
        ACCOUNT_LOCKED("Account is locked"),
        ACCOUNT_SUSPENDED("Account is suspended"),
        SESSION_EXPIRED("Session has expired"),
        UNAUTHORIZED_OPERATION("Unauthorized operation");

        private final String description;

        AuthFailureReason(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }

    public AuthenticationException(String userId, AuthFailureReason reason) {
        super(String.format("Authentication failed for user %s: %s", userId, reason.getDescription()),
              "AUTH_ERROR");
        this.userId = userId;
        this.reason = reason;
    }

    public AuthenticationException(AuthFailureReason reason) {
        super(reason.getDescription(), "AUTH_ERROR");
        this.userId = null;
        this.reason = reason;
    }

    public String getUserId() {
        return userId;
    }

    public AuthFailureReason getReason() {
        return reason;
    }
}



