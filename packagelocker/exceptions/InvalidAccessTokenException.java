package packagelocker.exceptions;

/**
 * Thrown when an access token is not found or is invalid.
 */
public class InvalidAccessTokenException extends LockerException {
    
    private final String accessCode;

    public InvalidAccessTokenException(String accessCode) {
        super(String.format("Invalid access code: '%s'. Please check and try again.", 
                maskCode(accessCode)));
        this.accessCode = accessCode;
    }

    public String getAccessCode() {
        return accessCode;
    }

    private static String maskCode(String code) {
        if (code == null || code.length() <= 4) {
            return "****";
        }
        return code.substring(0, 4) + "****";
    }
}
