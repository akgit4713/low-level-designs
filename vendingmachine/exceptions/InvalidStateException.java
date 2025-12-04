package vendingmachine.exceptions;

/**
 * Exception thrown when an operation is attempted in an invalid state.
 */
public class InvalidStateException extends VendingMachineException {
    
    private final String stateName;
    private final String operation;

    public InvalidStateException(String stateName, String operation) {
        super(String.format("Cannot perform '%s' in current state: %s", operation, stateName));
        this.stateName = stateName;
        this.operation = operation;
    }

    public String getStateName() {
        return stateName;
    }

    public String getOperation() {
        return operation;
    }
}
