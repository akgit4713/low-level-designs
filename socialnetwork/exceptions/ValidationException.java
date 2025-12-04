package socialnetwork.exceptions;

/**
 * Exception thrown when validation fails.
 */
public class ValidationException extends SocialNetworkException {
    
    public ValidationException(String message) {
        super(message);
    }

    public static ValidationException emailAlreadyExists() {
        return new ValidationException("Email address is already registered");
    }

    public static ValidationException invalidEmail() {
        return new ValidationException("Invalid email format");
    }

    public static ValidationException weakPassword() {
        return new ValidationException("Password must be at least 8 characters with letters and numbers");
    }

    public static ValidationException emptyContent() {
        return new ValidationException("Content cannot be empty");
    }
}



