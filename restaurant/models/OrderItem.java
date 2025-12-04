package restaurant.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an individual item within an order
 */
public class OrderItem {
    private final MenuItem menuItem;
    private final int quantity;
    private final String specialInstructions;
    private final BigDecimal itemTotal;

    public OrderItem(MenuItem menuItem, int quantity, String specialInstructions) {
        this.menuItem = Objects.requireNonNull(menuItem, "Menu item cannot be null");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        this.specialInstructions = specialInstructions != null ? specialInstructions : "";
        this.itemTotal = menuItem.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    public OrderItem(MenuItem menuItem, int quantity) {
        this(menuItem, quantity, null);
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public BigDecimal getItemTotal() {
        return itemTotal;
    }

    public int getTotalPreparationTime() {
        return menuItem.getPreparationTimeMinutes() * quantity;
    }

    @Override
    public String toString() {
        return String.format("%dx %s = %s%s", 
            quantity, 
            menuItem.getName(), 
            itemTotal,
            specialInstructions.isEmpty() ? "" : " [" + specialInstructions + "]");
    }
}

