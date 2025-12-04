package vendingmachine.models;

import java.util.Objects;

/**
 * Immutable representation of a product in the vending machine.
 * Contains product code, name, and price.
 */
public class Product {
    
    private final String code;
    private final String name;
    private final int price;

    public Product(String code, String name, int price) {
        if (code == null || code.isBlank()) {
            throw new IllegalArgumentException("Product code cannot be null or empty");
        }
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Product name cannot be null or empty");
        }
        if (price <= 0) {
            throw new IllegalArgumentException("Product price must be positive");
        }
        this.code = code.toUpperCase();
        this.name = name;
        this.price = price;
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Product product = (Product) o;
        return code.equals(product.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return String.format("[%s] %s - ₹%d", code, name, price);
    }
}
