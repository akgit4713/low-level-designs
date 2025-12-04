package fooddelivery.repositories;

import fooddelivery.enums.OrderStatus;
import fooddelivery.models.Order;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Order entities.
 */
public interface OrderRepository {
    Order save(Order order);
    Optional<Order> findById(String id);
    List<Order> findByCustomerId(String customerId);
    List<Order> findByRestaurantId(String restaurantId);
    List<Order> findByStatus(OrderStatus status);
    List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status);
    List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status);
    List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end);
    List<Order> findAll();
    void delete(String id);
    boolean existsById(String id);
}



