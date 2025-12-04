package atm.exceptions;

/**
 * Exception thrown when authentication fails.
 */
public class AuthenticationException extends ATMException {
    
    private final int remainingAttempts;

    public AuthenticationException(String message) {
        super(message);
        this.remainingAttempts = -1;
    }

    public AuthenticationException(String message, int remainingAttempts) {
        super(message);
        this.remainingAttempts = remainingAttempts;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public boolean isCardBlocked() {
        return remainingAttempts == 0;
    }
}



