package onlineshopping.models;

import onlineshopping.exceptions.CartException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Represents a user's shopping cart
 * Thread-safe for concurrent modifications
 */
public class Cart {
    private static final int MAX_QUANTITY_PER_ITEM = 10;

    private final String userId;
    private final Map<String, CartItem> items; // productId -> CartItem
    private final ReentrantLock lock = new ReentrantLock();
    private LocalDateTime lastModified;

    public Cart(String userId) {
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.items = new HashMap<>();
        this.lastModified = LocalDateTime.now();
    }

    public String getUserId() {
        return userId;
    }

    public LocalDateTime getLastModified() {
        return lastModified;
    }

    /**
     * Add a product to cart or update quantity if already exists
     */
    public void addItem(Product product, int quantity) {
        lock.lock();
        try {
            if (quantity <= 0) {
                throw CartException.invalidQuantity(quantity);
            }

            CartItem existingItem = items.get(product.getId());
            if (existingItem != null) {
                int newQuantity = existingItem.getQuantity() + quantity;
                if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                    throw CartException.maxQuantityExceeded(product.getId(), MAX_QUANTITY_PER_ITEM);
                }
                existingItem.setQuantity(newQuantity);
            } else {
                if (quantity > MAX_QUANTITY_PER_ITEM) {
                    throw CartException.maxQuantityExceeded(product.getId(), MAX_QUANTITY_PER_ITEM);
                }
                items.put(product.getId(), new CartItem(product, quantity));
            }
            lastModified = LocalDateTime.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Update quantity for an existing item
     */
    public void updateItemQuantity(String productId, int newQuantity) {
        lock.lock();
        try {
            CartItem item = items.get(productId);
            if (item == null) {
                throw CartException.itemNotFound(productId);
            }
            if (newQuantity <= 0) {
                items.remove(productId);
            } else {
                if (newQuantity > MAX_QUANTITY_PER_ITEM) {
                    throw CartException.maxQuantityExceeded(productId, MAX_QUANTITY_PER_ITEM);
                }
                item.setQuantity(newQuantity);
            }
            lastModified = LocalDateTime.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Remove an item from cart
     */
    public void removeItem(String productId) {
        lock.lock();
        try {
            if (!items.containsKey(productId)) {
                throw CartException.itemNotFound(productId);
            }
            items.remove(productId);
            lastModified = LocalDateTime.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Clear all items from cart
     */
    public void clear() {
        lock.lock();
        try {
            items.clear();
            lastModified = LocalDateTime.now();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get all cart items
     */
    public List<CartItem> getItems() {
        lock.lock();
        try {
            return Collections.unmodifiableList(new ArrayList<>(items.values()));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get specific cart item by product ID
     */
    public Optional<CartItem> getItem(String productId) {
        lock.lock();
        try {
            return Optional.ofNullable(items.get(productId));
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if cart contains a product
     */
    public boolean containsProduct(String productId) {
        lock.lock();
        try {
            return items.containsKey(productId);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get total number of items in cart
     */
    public int getItemCount() {
        lock.lock();
        try {
            return items.values().stream()
                .mapToInt(CartItem::getQuantity)
                .sum();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Get number of unique products in cart
     */
    public int getUniqueItemCount() {
        lock.lock();
        try {
            return items.size();
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calculate cart subtotal (before discounts/shipping)
     */
    public BigDecimal getSubtotal() {
        lock.lock();
        try {
            return items.values().stream()
                .map(CartItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Calculate total savings from discounts
     */
    public BigDecimal getTotalSavings() {
        lock.lock();
        try {
            return items.values().stream()
                .map(CartItem::getSavings)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        } finally {
            lock.unlock();
        }
    }

    /**
     * Check if cart is empty
     */
    public boolean isEmpty() {
        lock.lock();
        try {
            return items.isEmpty();
        } finally {
            lock.unlock();
        }
    }

    @Override
    public String toString() {
        return String.format("Cart{userId='%s', items=%d, subtotal=%s}", 
            userId, getItemCount(), getSubtotal());
    }
}



