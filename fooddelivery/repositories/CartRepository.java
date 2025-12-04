package fooddelivery.repositories;

import fooddelivery.models.Cart;
import java.util.Optional;

/**
 * Repository interface for Cart entities.
 */
public interface CartRepository {
    Cart save(Cart cart);
    Optional<Cart> findById(String id);
    Optional<Cart> findByCustomerId(String customerId);
    void delete(String id);
    void deleteByCustomerId(String customerId);
    boolean existsById(String id);
}



