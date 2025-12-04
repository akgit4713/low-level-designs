package vendingmachine.exceptions;

/**
 * Exception thrown when the machine cannot provide exact change.
 */
public class InsufficientChangeException extends VendingMachineException {
    
    private final int changeRequired;

    public InsufficientChangeException(int changeRequired) {
        super(String.format("Cannot provide exact change of ₹%d. Please use exact amount.", changeRequired));
        this.changeRequired = changeRequired;
    }

    public int getChangeRequired() {
        return changeRequired;
    }
}
