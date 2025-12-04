package fooddelivery.models;

import fooddelivery.exceptions.CartException;
import java.math.BigDecimal;
import java.util.*;

/**
 * Shopping cart for a customer, tied to a single restaurant.
 */
public class Cart {
    private final String id;
    private final String customerId;
    private String restaurantId;
    private final Map<String, OrderItem> items;

    public Cart(String id, String customerId) {
        this.id = id;
        this.customerId = customerId;
        this.items = new HashMap<>();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public void addItem(MenuItem menuItem, int quantity) {
        // Ensure cart contains items from only one restaurant
        if (restaurantId == null) {
            restaurantId = menuItem.getRestaurantId();
        } else if (!restaurantId.equals(menuItem.getRestaurantId())) {
            throw new CartException("Cannot add items from different restaurants. Clear cart first.");
        }

        String itemId = menuItem.getId();
        if (items.containsKey(itemId)) {
            OrderItem existing = items.get(itemId);
            existing.setQuantity(existing.getQuantity() + quantity);
        } else {
            items.put(itemId, new OrderItem(menuItem, quantity));
        }
    }

    public void removeItem(String menuItemId) {
        items.remove(menuItemId);
        if (items.isEmpty()) {
            restaurantId = null;
        }
    }

    public void updateItemQuantity(String menuItemId, int quantity) {
        if (!items.containsKey(menuItemId)) {
            throw new CartException("Item not found in cart: " + menuItemId);
        }
        if (quantity <= 0) {
            removeItem(menuItemId);
        } else {
            items.get(menuItemId).setQuantity(quantity);
        }
    }

    public List<OrderItem> getItems() {
        return new ArrayList<>(items.values());
    }

    public OrderItem getItem(String menuItemId) {
        return items.get(menuItemId);
    }

    public BigDecimal getSubtotal() {
        return items.values().stream()
                .map(OrderItem::getSubtotal)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public int getTotalItems() {
        return items.values().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum();
    }

    public boolean isEmpty() {
        return items.isEmpty();
    }

    public void clear() {
        items.clear();
        restaurantId = null;
    }

    @Override
    public String toString() {
        return String.format("Cart{id='%s', restaurant='%s', items=%d, subtotal=%s}", 
            id, restaurantId, getTotalItems(), getSubtotal());
    }
}



