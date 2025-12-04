package onlineshopping.services;

import onlineshopping.models.Cart;
import onlineshopping.models.CartItem;
import onlineshopping.models.Product;

import java.util.List;

/**
 * Service interface for shopping cart management
 */
public interface CartService {
    
    /**
     * Get or create cart for user
     */
    Cart getCart(String userId);
    
    /**
     * Add item to cart
     */
    void addToCart(String userId, Product product, int quantity);
    
    /**
     * Update item quantity
     */
    void updateQuantity(String userId, String productId, int newQuantity);
    
    /**
     * Remove item from cart
     */
    void removeFromCart(String userId, String productId);
    
    /**
     * Clear cart
     */
    void clearCart(String userId);
    
    /**
     * Get cart items
     */
    List<CartItem> getCartItems(String userId);
    
    /**
     * Get cart item count
     */
    int getCartItemCount(String userId);
    
    /**
     * Validate cart (check stock availability)
     */
    boolean validateCart(String userId);
}



