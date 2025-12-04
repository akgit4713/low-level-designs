package vendingmachine.exceptions;

/**
 * Base exception for all vending machine related exceptions.
 * Provides a common parent for exception handling.
 */
public class VendingMachineException extends RuntimeException {
    
    public VendingMachineException(String message) {
        super(message);
    }

    public VendingMachineException(String message, Throwable cause) {
        super(message, cause);
    }
}
