package digitalwallet.strategies.validation;

import digitalwallet.models.Transfer;
import digitalwallet.models.Wallet;

/**
 * Strategy interface for transfer validation.
 * Allows different validation rules to be plugged in.
 */
public interface TransferValidationStrategy {
    
    /**
     * Validate a transfer
     * @param transfer The transfer to validate
     * @param sourceWallet The source wallet
     * @param targetWallet The target wallet (may be null for external transfers)
     * @return ValidationResult with success/failure and message
     */
    ValidationResult validate(Transfer transfer, Wallet sourceWallet, Wallet targetWallet);
    
    /**
     * Get the name of this validation strategy
     */
    String getStrategyName();
    
    /**
     * Get the order in which this validator should run (lower = earlier)
     */
    default int getOrder() {
        return 100;
    }

    /**
     * Result of a validation check
     */
    class ValidationResult {
        private final boolean valid;
        private final String message;
        private final String errorCode;

        private ValidationResult(boolean valid, String message, String errorCode) {
            this.valid = valid;
            this.message = message;
            this.errorCode = errorCode;
        }

        public static ValidationResult success() {
            return new ValidationResult(true, "Validation passed", null);
        }

        public static ValidationResult failure(String message) {
            return new ValidationResult(false, message, "VALIDATION_FAILED");
        }

        public static ValidationResult failure(String message, String errorCode) {
            return new ValidationResult(false, message, errorCode);
        }

        public boolean isValid() { return valid; }
        public String getMessage() { return message; }
        public String getErrorCode() { return errorCode; }

        @Override
        public String toString() {
            return valid ? "Valid" : String.format("Invalid: %s (%s)", message, errorCode);
        }
    }
}



