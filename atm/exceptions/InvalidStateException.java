package atm.exceptions;

import atm.enums.ATMStateType;

/**
 * Exception thrown when an operation is attempted in an invalid ATM state.
 */
public class InvalidStateException extends ATMException {
    
    private final ATMStateType currentState;
    private final String attemptedOperation;

    public InvalidStateException(ATMStateType currentState, String attemptedOperation) {
        super("Cannot perform '" + attemptedOperation + "' in state: " + currentState);
        this.currentState = currentState;
        this.attemptedOperation = attemptedOperation;
    }

    public ATMStateType getCurrentState() {
        return currentState;
    }

    public String getAttemptedOperation() {
        return attemptedOperation;
    }
}



