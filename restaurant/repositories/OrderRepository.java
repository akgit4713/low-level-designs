package restaurant.repositories;

import restaurant.enums.OrderStatus;
import restaurant.models.Order;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Order entities
 */
public interface OrderRepository extends Repository<Order, String> {
    
    /**
     * Find orders by status
     */
    List<Order> findByStatus(OrderStatus status);
    
    /**
     * Find orders by customer
     */
    List<Order> findByCustomerId(String customerId);
    
    /**
     * Find orders by table
     */
    List<Order> findByTableId(String tableId);
    
    /**
     * Find orders within a time range
     */
    List<Order> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Find active orders (not completed or cancelled)
     */
    List<Order> findActiveOrders();
}

