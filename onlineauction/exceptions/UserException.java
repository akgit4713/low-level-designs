package onlineauction.exceptions;

/**
 * Exception for user-related errors.
 */
public class UserException extends RuntimeException {
    
    private final String userId;
    private final ErrorCode errorCode;
    
    public UserException(String message) {
        super(message);
        this.userId = null;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public UserException(String message, ErrorCode errorCode) {
        super(message);
        this.userId = null;
        this.errorCode = errorCode;
    }
    
    public UserException(String message, String userId, ErrorCode errorCode) {
        super(message);
        this.userId = userId;
        this.errorCode = errorCode;
    }
    
    public UserException(String message, Throwable cause) {
        super(message, cause);
        this.userId = null;
        this.errorCode = ErrorCode.GENERAL_ERROR;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public ErrorCode getErrorCode() {
        return errorCode;
    }
    
    public enum ErrorCode {
        GENERAL_ERROR,
        USER_NOT_FOUND,
        USERNAME_ALREADY_EXISTS,
        EMAIL_ALREADY_EXISTS,
        INVALID_CREDENTIALS,
        INVALID_EMAIL,
        INVALID_USERNAME,
        USER_NOT_AUTHENTICATED
    }
}



