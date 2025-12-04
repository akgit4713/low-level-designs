package vendingmachine.exceptions;

/**
 * Exception thrown when an invalid or unknown product code is provided.
 */
public class InvalidProductException extends VendingMachineException {
    
    private final String productCode;

    public InvalidProductException(String productCode) {
        super(String.format("Invalid product code: '%s'", productCode));
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
