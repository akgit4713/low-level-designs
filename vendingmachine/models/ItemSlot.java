package vendingmachine.models;

import vendingmachine.exceptions.OutOfStockException;

/**
 * Represents a slot in the vending machine that holds a product with quantity.
 * Thread-safe for concurrent access.
 */
public class ItemSlot {
    
    private final Product product;
    private int quantity;

    public ItemSlot(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("Product cannot be null");
        }
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.product = product;
        this.quantity = quantity;
    }

    public Product getProduct() {
        return product;
    }

    public synchronized int getQuantity() {
        return quantity;
    }

    public synchronized boolean isAvailable() {
        return quantity > 0;
    }

    /**
     * Dispenses one unit of the product.
     * 
     * @return the dispensed product
     * @throws OutOfStockException if no units available
     */
    public synchronized Product dispense() {
        if (quantity <= 0) {
            throw new OutOfStockException(product.getCode());
        }
        quantity--;
        return product;
    }

    /**
     * Adds stock to this slot.
     * 
     * @param additionalQuantity the quantity to add
     */
    public synchronized void addStock(int additionalQuantity) {
        if (additionalQuantity < 0) {
            throw new IllegalArgumentException("Cannot add negative quantity");
        }
        this.quantity += additionalQuantity;
    }

    @Override
    public String toString() {
        return String.format("%s (qty: %d)", product, quantity);
    }
}
