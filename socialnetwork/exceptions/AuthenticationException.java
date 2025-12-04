package socialnetwork.exceptions;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends SocialNetworkException {
    
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static AuthenticationException invalidCredentials() {
        return new AuthenticationException("Invalid email or password");
    }

    public static AuthenticationException sessionExpired() {
        return new AuthenticationException("Session has expired. Please log in again");
    }

    public static AuthenticationException invalidSession() {
        return new AuthenticationException("Invalid or expired session token");
    }

    public static AuthenticationException accountDeactivated() {
        return new AuthenticationException("Account is deactivated or suspended");
    }
}



