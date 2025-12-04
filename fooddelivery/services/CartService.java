package fooddelivery.services;

import fooddelivery.models.Cart;
import fooddelivery.models.MenuItem;
import java.util.Optional;

/**
 * Service interface for shopping cart operations.
 */
public interface CartService {
    Cart getOrCreateCart(String customerId);
    Optional<Cart> getCart(String customerId);
    void addToCart(String customerId, MenuItem item, int quantity);
    void removeFromCart(String customerId, String menuItemId);
    void updateQuantity(String customerId, String menuItemId, int quantity);
    void clearCart(String customerId);
}



