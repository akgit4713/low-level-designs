package onlineshopping.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents an item in the shopping cart
 */
public class CartItem {
    private final Product product;
    private final LocalDateTime addedAt;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = Objects.requireNonNull(product, "Product is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
        this.addedAt = LocalDateTime.now();
    }

    public Product getProduct() {
        return product;
    }

    public String getProductId() {
        return product.getId();
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public void incrementQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        this.quantity += amount;
    }

    public void decrementQuantity(int amount) {
        if (amount <= 0) {
            throw new IllegalArgumentException("Amount must be positive");
        }
        if (this.quantity - amount <= 0) {
            throw new IllegalArgumentException("Cannot reduce quantity below 1");
        }
        this.quantity -= amount;
    }

    public LocalDateTime getAddedAt() {
        return addedAt;
    }

    /**
     * Calculate subtotal for this cart item
     */
    public BigDecimal getSubtotal() {
        return product.getPrice().multiply(BigDecimal.valueOf(quantity));
    }

    /**
     * Calculate savings if product has a discount
     */
    public BigDecimal getSavings() {
        if (!product.hasDiscount()) {
            return BigDecimal.ZERO;
        }
        return product.getDiscountAmount().multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CartItem cartItem = (CartItem) o;
        return Objects.equals(product.getId(), cartItem.product.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(product.getId());
    }

    @Override
    public String toString() {
        return String.format("CartItem{product='%s', quantity=%d, subtotal=%s}", 
            product.getName(), quantity, getSubtotal());
    }
}



