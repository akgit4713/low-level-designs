package onlineshopping.models;

import onlineshopping.exceptions.InventoryException;

import java.time.LocalDateTime;
import java.util.Objects;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Represents inventory for a product
 * Thread-safe for concurrent stock operations
 */
public class Inventory {
    private static final int DEFAULT_LOW_STOCK_THRESHOLD = 10;

    private final String productId;
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    
    private int totalQuantity;
    private int reservedQuantity;
    private int lowStockThreshold;
    private LocalDateTime lastUpdated;

    public Inventory(String productId, int initialQuantity) {
        this.productId = Objects.requireNonNull(productId, "Product ID is required");
        if (initialQuantity < 0) {
            throw InventoryException.negativeQuantity(productId);
        }
        this.totalQuantity = initialQuantity;
        this.reservedQuantity = 0;
        this.lowStockThreshold = DEFAULT_LOW_STOCK_THRESHOLD;
        this.lastUpdated = LocalDateTime.now();
    }

    public String getProductId() {
        return productId;
    }

    public int getTotalQuantity() {
        lock.readLock().lock();
        try {
            return totalQuantity;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getReservedQuantity() {
        lock.readLock().lock();
        try {
            return reservedQuantity;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Get available quantity (total - reserved)
     */
    public int getAvailableQuantity() {
        lock.readLock().lock();
        try {
            return totalQuantity - reservedQuantity;
        } finally {
            lock.readLock().unlock();
        }
    }

    public int getLowStockThreshold() {
        return lowStockThreshold;
    }

    public void setLowStockThreshold(int threshold) {
        this.lowStockThreshold = threshold;
    }

    public LocalDateTime getLastUpdated() {
        return lastUpdated;
    }

    /**
     * Check if stock is available for given quantity
     */
    public boolean isAvailable(int quantity) {
        lock.readLock().lock();
        try {
            return getAvailableQuantity() >= quantity;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Check if stock is low
     */
    public boolean isLowStock() {
        lock.readLock().lock();
        try {
            return getAvailableQuantity() <= lowStockThreshold;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Check if product is out of stock
     */
    public boolean isOutOfStock() {
        lock.readLock().lock();
        try {
            return getAvailableQuantity() <= 0;
        } finally {
            lock.readLock().unlock();
        }
    }

    /**
     * Reserve stock for an order
     * @return true if reservation was successful
     */
    public boolean reserve(int quantity) {
        lock.writeLock().lock();
        try {
            if (getAvailableQuantity() < quantity) {
                return false;
            }
            reservedQuantity += quantity;
            lastUpdated = LocalDateTime.now();
            return true;
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Release reserved stock (e.g., on order cancellation)
     */
    public void releaseReservation(int quantity) {
        lock.writeLock().lock();
        try {
            if (quantity > reservedQuantity) {
                throw new IllegalArgumentException("Cannot release more than reserved");
            }
            reservedQuantity -= quantity;
            lastUpdated = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Confirm stock deduction (convert reservation to actual sale)
     */
    public void confirmDeduction(int quantity) {
        lock.writeLock().lock();
        try {
            if (quantity > reservedQuantity) {
                throw new IllegalArgumentException("Cannot confirm more than reserved");
            }
            reservedQuantity -= quantity;
            totalQuantity -= quantity;
            lastUpdated = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Add stock (e.g., new inventory arrival)
     */
    public void addStock(int quantity) {
        lock.writeLock().lock();
        try {
            if (quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be positive");
            }
            totalQuantity += quantity;
            lastUpdated = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    /**
     * Set total stock quantity directly
     */
    public void setTotalQuantity(int quantity) {
        lock.writeLock().lock();
        try {
            if (quantity < 0) {
                throw InventoryException.negativeQuantity(productId);
            }
            if (quantity < reservedQuantity) {
                throw new IllegalArgumentException("Cannot set quantity below reserved amount");
            }
            totalQuantity = quantity;
            lastUpdated = LocalDateTime.now();
        } finally {
            lock.writeLock().unlock();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Inventory inventory = (Inventory) o;
        return Objects.equals(productId, inventory.productId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(productId);
    }

    @Override
    public String toString() {
        return String.format("Inventory{productId='%s', available=%d, reserved=%d, total=%d}", 
            productId, getAvailableQuantity(), reservedQuantity, totalQuantity);
    }
}



