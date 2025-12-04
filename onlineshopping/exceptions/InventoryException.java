package onlineshopping.exceptions;

/**
 * Exception for inventory-related errors
 */
public class InventoryException extends ShoppingException {

    public InventoryException(String message) {
        super(message);
    }

    public static InventoryException insufficientStock(String productId, int requested, int available) {
        return new InventoryException(
            String.format("Insufficient stock for %s: requested %d, available %d", 
                productId, requested, available)
        );
    }

    public static InventoryException notFound(String productId) {
        return new InventoryException("Inventory not found for product: " + productId);
    }

    public static InventoryException negativeQuantity(String productId) {
        return new InventoryException("Cannot set negative quantity for product: " + productId);
    }

    public static InventoryException reservationFailed(String productId) {
        return new InventoryException("Failed to reserve stock for product: " + productId);
    }
}



