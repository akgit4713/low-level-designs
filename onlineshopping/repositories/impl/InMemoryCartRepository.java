package onlineshopping.repositories.impl;

import onlineshopping.models.Cart;
import onlineshopping.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of cart repository
 * Carts are stored per user
 */
public class InMemoryCartRepository implements Repository<Cart, String> {
    
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();

    @Override
    public Cart save(Cart cart) {
        carts.put(cart.getUserId(), cart);
        return cart;
    }

    @Override
    public Optional<Cart> findById(String userId) {
        return Optional.ofNullable(carts.get(userId));
    }

    @Override
    public List<Cart> findAll() {
        return new ArrayList<>(carts.values());
    }

    @Override
    public boolean deleteById(String userId) {
        return carts.remove(userId) != null;
    }

    @Override
    public boolean existsById(String userId) {
        return carts.containsKey(userId);
    }

    @Override
    public long count() {
        return carts.size();
    }

    /**
     * Get or create a cart for a user
     */
    public Cart getOrCreate(String userId) {
        return carts.computeIfAbsent(userId, Cart::new);
    }

    /**
     * Clear user's cart
     */
    public void clearCart(String userId) {
        Cart cart = carts.get(userId);
        if (cart != null) {
            cart.clear();
        }
    }
}



