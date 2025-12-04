package onlineshopping.services;

import onlineshopping.enums.OrderStatus;
import onlineshopping.enums.PaymentMethod;
import onlineshopping.enums.ShippingMethod;
import onlineshopping.models.Address;
import onlineshopping.models.Order;
import onlineshopping.observers.OrderObserver;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for order management
 */
public interface OrderService {
    
    /**
     * Place a new order from user's cart
     */
    Order placeOrder(String userId, Address shippingAddress, ShippingMethod shippingMethod, 
                     PaymentMethod paymentMethod);
    
    /**
     * Get order by ID
     */
    Optional<Order> getOrder(String orderId);
    
    /**
     * Get orders for a user
     */
    List<Order> getUserOrders(String userId);
    
    /**
     * Get orders by status
     */
    List<Order> getOrdersByStatus(OrderStatus status);
    
    /**
     * Update order status
     */
    void updateOrderStatus(String orderId, OrderStatus newStatus);
    
    /**
     * Cancel order
     */
    void cancelOrder(String orderId, String reason);
    
    /**
     * Confirm order (after payment)
     */
    void confirmOrder(String orderId);
    
    /**
     * Ship order
     */
    void shipOrder(String orderId, String trackingNumber);
    
    /**
     * Mark order as delivered
     */
    void markDelivered(String orderId);
    
    /**
     * Get order by tracking number
     */
    Optional<Order> getOrderByTracking(String trackingNumber);
    
    /**
     * Get orders within date range
     */
    List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end);
    
    /**
     * Register order observer
     */
    void addObserver(OrderObserver observer);
    
    /**
     * Remove order observer
     */
    void removeObserver(OrderObserver observer);
}



