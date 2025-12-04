package restaurant.models;

import java.time.LocalDate;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents inventory stock of an ingredient
 * Thread-safe for concurrent stock updates
 */
public class InventoryItem {
    private final Ingredient ingredient;
    private volatile double quantity;
    private final double reorderLevel;
    private final double reorderQuantity;
    private LocalDate lastRestocked;
    private final ReentrantLock lock = new ReentrantLock();

    public InventoryItem(Ingredient ingredient, double initialQuantity, 
                         double reorderLevel, double reorderQuantity) {
        this.ingredient = Objects.requireNonNull(ingredient, "Ingredient cannot be null");
        if (initialQuantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative");
        }
        this.quantity = initialQuantity;
        this.reorderLevel = reorderLevel;
        this.reorderQuantity = reorderQuantity;
        this.lastRestocked = LocalDate.now();
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public double getQuantity() {
        return quantity;
    }

    public double getReorderLevel() {
        return reorderLevel;
    }

    public double getReorderQuantity() {
        return reorderQuantity;
    }

    public LocalDate getLastRestocked() {
        return lastRestocked;
    }

    /**
     * Check if stock is below reorder level
     */
    public boolean needsReorder() {
        return quantity <= reorderLevel;
    }

    /**
     * Check if there's sufficient stock for required quantity
     */
    public boolean hasSufficientStock(double required) {
        return quantity >= required;
    }

    /**
     * Consume stock - thread-safe
     * @param amount Amount to consume
     * @return true if consumption successful, false if insufficient stock
     */
    public boolean consume(double amount) {
        lock.lock();
        try {
            if (quantity >= amount) {
                quantity -= amount;
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    /**
     * Restock inventory - thread-safe
     * @param amount Amount to add
     */
    public void restock(double amount) {
        lock.lock();
        try {
            if (amount < 0) {
                throw new IllegalArgumentException("Restock amount cannot be negative");
            }
            quantity += amount;
            lastRestocked = LocalDate.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get current stock level as percentage of reorder quantity
     */
    public double getStockPercentage() {
        return (quantity / reorderQuantity) * 100;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InventoryItem that = (InventoryItem) o;
        return Objects.equals(ingredient.getId(), that.ingredient.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(ingredient.getId());
    }

    @Override
    public String toString() {
        return String.format("InventoryItem{ingredient='%s', qty=%.2f %s, needsReorder=%s}",
            ingredient.getName(), quantity, ingredient.getUnit(), needsReorder());
    }
}

