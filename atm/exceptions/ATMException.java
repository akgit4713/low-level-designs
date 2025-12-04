package atm.exceptions;

/**
 * Base exception for all ATM-related errors.
 */
public class ATMException extends RuntimeException {
    
    public ATMException(String message) {
        super(message);
    }

    public ATMException(String message, Throwable cause) {
        super(message, cause);
    }
}



