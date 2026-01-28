package packagelocker.exceptions;

/**
 * Thrown when an access token has already been used.
 */
public class AlreadyUsedAccessTokenException extends LockerException {
    
    private final String accessCode;

    public AlreadyUsedAccessTokenException(String accessCode) {
        super("This access code has already been used. Package was previously retrieved.");
        this.accessCode = accessCode;
    }

    public String getAccessCode() {
        return accessCode;
    }
}
