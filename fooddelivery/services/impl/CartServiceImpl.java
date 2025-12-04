package fooddelivery.services.impl;

import fooddelivery.exceptions.CartException;
import fooddelivery.models.Cart;
import fooddelivery.models.MenuItem;
import fooddelivery.repositories.CartRepository;
import fooddelivery.services.CartService;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of CartService.
 */
public class CartServiceImpl implements CartService {
    
    private final CartRepository cartRepository;
    
    public CartServiceImpl(CartRepository cartRepository) {
        this.cartRepository = cartRepository;
    }

    @Override
    public Cart getOrCreateCart(String customerId) {
        return cartRepository.findByCustomerId(customerId)
                .orElseGet(() -> {
                    String cartId = "CART-" + UUID.randomUUID().toString().substring(0, 8);
                    Cart cart = new Cart(cartId, customerId);
                    return cartRepository.save(cart);
                });
    }

    @Override
    public Optional<Cart> getCart(String customerId) {
        return cartRepository.findByCustomerId(customerId);
    }

    @Override
    public void addToCart(String customerId, MenuItem item, int quantity) {
        if (!item.isAvailable()) {
            throw new CartException("Item is not available: " + item.getName());
        }
        if (quantity <= 0) {
            throw new CartException("Quantity must be positive");
        }
        
        Cart cart = getOrCreateCart(customerId);
        cart.addItem(item, quantity);
        cartRepository.save(cart);
    }

    @Override
    public void removeFromCart(String customerId, String menuItemId) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found for customer: " + customerId));
        
        cart.removeItem(menuItemId);
        cartRepository.save(cart);
    }

    @Override
    public void updateQuantity(String customerId, String menuItemId, int quantity) {
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found for customer: " + customerId));
        
        cart.updateItemQuantity(menuItemId, quantity);
        cartRepository.save(cart);
    }

    @Override
    public void clearCart(String customerId) {
        cartRepository.findByCustomerId(customerId)
                .ifPresent(cart -> {
                    cart.clear();
                    cartRepository.save(cart);
                });
    }
}



