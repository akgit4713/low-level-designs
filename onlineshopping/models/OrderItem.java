package onlineshopping.models;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Represents a line item in an order
 * Captures product details at time of purchase (price may change later)
 */
public class OrderItem {
    private final String productId;
    private final String productName;
    private final String sellerId;
    private final BigDecimal unitPrice;
    private final int quantity;

    public OrderItem(Product product, int quantity) {
        Objects.requireNonNull(product, "Product is required");
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        
        this.productId = product.getId();
        this.productName = product.getName();
        this.sellerId = product.getSellerId();
        this.unitPrice = product.getPrice();
        this.quantity = quantity;
    }

    public OrderItem(String productId, String productName, String sellerId, 
                     BigDecimal unitPrice, int quantity) {
        this.productId = Objects.requireNonNull(productId);
        this.productName = Objects.requireNonNull(productName);
        this.sellerId = Objects.requireNonNull(sellerId);
        this.unitPrice = Objects.requireNonNull(unitPrice);
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be positive");
        }
        this.quantity = quantity;
    }

    public String getProductId() {
        return productId;
    }

    public String getProductName() {
        return productName;
    }

    public String getSellerId() {
        return sellerId;
    }

    public BigDecimal getUnitPrice() {
        return unitPrice;
    }

    public int getQuantity() {
        return quantity;
    }

    /**
     * Calculate total for this line item
     */
    public BigDecimal getTotal() {
        return unitPrice.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        OrderItem orderItem = (OrderItem) o;
        return Objects.equals(productId, orderItem.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("OrderItem{product='%s', qty=%d, price=%s, total=%s}", 
            productName, quantity, unitPrice, getTotal());
    }
}



