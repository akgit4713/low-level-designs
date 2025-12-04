package vendingmachine.exceptions;

/**
 * Exception thrown when the user has not inserted enough money
 * to purchase the selected product.
 */
public class InsufficientFundsException extends VendingMachineException {
    
    private final int required;
    private final int available;

    public InsufficientFundsException(int required, int available) {
        super(String.format("Insufficient funds. Required: ₹%d, Available: ₹%d, Need: ₹%d more",
                required, available, required - available));
        this.required = required;
        this.available = available;
    }

    public int getRequired() {
        return required;
    }

    public int getAvailable() {
        return available;
    }

    public int getShortfall() {
        return required - available;
    }
}
