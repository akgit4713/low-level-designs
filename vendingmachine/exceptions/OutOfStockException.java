package vendingmachine.exceptions;

/**
 * Exception thrown when the selected product is out of stock.
 */
public class OutOfStockException extends VendingMachineException {
    
    private final String productCode;

    public OutOfStockException(String productCode) {
        super(String.format("Product '%s' is out of stock", productCode));
        this.productCode = productCode;
    }

    public String getProductCode() {
        return productCode;
    }
}
