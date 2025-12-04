package librarymanagement.strategies.borrowing;

/**
 * Result of a borrowing rule validation.
 */
public class ValidationResult {
    
    private final boolean valid;
    private final String message;
    private final String ruleName;

    private ValidationResult(boolean valid, String message, String ruleName) {
        this.valid = valid;
        this.message = message;
        this.ruleName = ruleName;
    }

    public static ValidationResult success() {
        return new ValidationResult(true, "Validation passed", null);
    }

    public static ValidationResult failure(String message, String ruleName) {
        return new ValidationResult(false, message, ruleName);
    }

    public boolean isValid() {
        return valid;
    }

    public String getMessage() {
        return message;
    }

    public String getRuleName() {
        return ruleName;
    }

    @Override
    public String toString() {
        return valid ? "VALID" : String.format("INVALID[%s]: %s", ruleName, message);
    }
}



