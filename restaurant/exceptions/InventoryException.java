package restaurant.exceptions;

/**
 * Exception for inventory-related errors
 */
public class InventoryException extends RestaurantException {

    public InventoryException(String message) {
        super(message);
    }

    public InventoryException(String message, Throwable cause) {
        super(message, cause);
    }

    public static InventoryException insufficientStock(String ingredientName, double required, double available) {
        return new InventoryException(
            String.format("Insufficient stock for %s: required %.2f, available %.2f", 
                ingredientName, required, available)
        );
    }

    public static InventoryException ingredientNotFound(String ingredientId) {
        return new InventoryException("Ingredient not found: " + ingredientId);
    }
}

