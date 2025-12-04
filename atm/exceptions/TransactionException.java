package atm.exceptions;

import atm.enums.TransactionStatus;

/**
 * Exception thrown when a transaction fails.
 */
public class TransactionException extends ATMException {
    
    private final TransactionStatus status;

    public TransactionException(String message, TransactionStatus status) {
        super(message);
        this.status = status;
    }

    public TransactionStatus getStatus() {
        return status;
    }
}



