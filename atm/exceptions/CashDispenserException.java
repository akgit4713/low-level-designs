package atm.exceptions;

import java.math.BigDecimal;

/**
 * Exception thrown when cash dispenser cannot fulfill a request.
 */
public class CashDispenserException extends ATMException {
    
    private final BigDecimal requestedAmount;
    private final BigDecimal availableAmount;

    public CashDispenserException(String message) {
        super(message);
        this.requestedAmount = null;
        this.availableAmount = null;
    }

    public CashDispenserException(String message, BigDecimal requestedAmount, BigDecimal availableAmount) {
        super(message);
        this.requestedAmount = requestedAmount;
        this.availableAmount = availableAmount;
    }

    public BigDecimal getRequestedAmount() {
        return requestedAmount;
    }

    public BigDecimal getAvailableAmount() {
        return availableAmount;
    }
}



