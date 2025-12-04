package restaurant.services;

import restaurant.enums.OrderStatus;
import restaurant.models.Order;
import restaurant.observers.OrderObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for order management
 */
public interface OrderService {
    
    /**
     * Place a new order
     */
    Order placeOrder(Order order);
    
    /**
     * Get order by ID
     */
    Optional<Order> getOrder(String orderId);
    
    /**
     * Update order status
     */
    void updateOrderStatus(String orderId, OrderStatus newStatus);
    
    /**
     * Cancel an order
     */
    void cancelOrder(String orderId);
    
    /**
     * Get orders by status
     */
    List<Order> getOrdersByStatus(OrderStatus status);
    
    /**
     * Get active orders
     */
    List<Order> getActiveOrders();
    
    /**
     * Get orders for a customer
     */
    List<Order> getOrdersForCustomer(String customerId);
    
    /**
     * Get orders within date range (for reporting)
     */
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Register an order observer
     */
    void addObserver(OrderObserver observer);
    
    /**
     * Remove an order observer
     */
    void removeObserver(OrderObserver observer);
}

