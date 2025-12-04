package fooddelivery.repositories.impl;

import fooddelivery.models.Cart;
import fooddelivery.repositories.CartRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of CartRepository.
 */
public class InMemoryCartRepository implements CartRepository {
    private final Map<String, Cart> carts = new ConcurrentHashMap<>();
    private final Map<String, String> customerIdIndex = new ConcurrentHashMap<>();

    @Override
    public Cart save(Cart cart) {
        carts.put(cart.getId(), cart);
        customerIdIndex.put(cart.getCustomerId(), cart.getId());
        return cart;
    }

    @Override
    public Optional<Cart> findById(String id) {
        return Optional.ofNullable(carts.get(id));
    }

    @Override
    public Optional<Cart> findByCustomerId(String customerId) {
        String cartId = customerIdIndex.get(customerId);
        return cartId != null ? findById(cartId) : Optional.empty();
    }

    @Override
    public void delete(String id) {
        Cart cart = carts.remove(id);
        if (cart != null) {
            customerIdIndex.remove(cart.getCustomerId());
        }
    }

    @Override
    public void deleteByCustomerId(String customerId) {
        String cartId = customerIdIndex.remove(customerId);
        if (cartId != null) {
            carts.remove(cartId);
        }
    }

    @Override
    public boolean existsById(String id) {
        return carts.containsKey(id);
    }
}



