package fooddelivery.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents an item within an order with quantity and special instructions.
 */
public class OrderItem {
    private final String menuItemId;
    private final String menuItemName;
    private final BigDecimal unitPrice;
    private int quantity;
    private String specialInstructions;

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItemId = menuItem.getId();
        this.menuItemName = menuItem.getName();
        this.unitPrice = menuItem.getPrice();
        this.quantity = quantity;
    }

    public String getMenuItemId() {
        return menuItemId;
    }

    public String getMenuItemName() {
        return menuItemName;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public void incrementQuantity() {
        this.quantity++;
    }

    public void decrementQuantity() {
        if (quantity > 1) {
            this.quantity--;
        }
    }

    public String getSpecialInstructions() {
        return specialInstructions;
    }

    public void setSpecialInstructions(String specialInstructions) {
        this.specialInstructions = specialInstructions;
    }

    public BigDecimal getSubtotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(menuItemId, orderItem.menuItemId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(menuItemId);
    }

    @Override
    public String toString() {
        return String.format("%s x%d = %s", menuItemName, quantity, getSubtotal());
    }
}



