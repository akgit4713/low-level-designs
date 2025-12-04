package restaurant.observers;

import restaurant.enums.OrderStatus;
import restaurant.models.Order;

/**
 * Observer interface for order state changes
 * Follows Observer Pattern - decouples order processing from reactions
 */
public interface OrderObserver {
    
    /**
     * Called when an order is created
     */
    void onOrderCreated(Order order);
    
    /**
     * Called when order status changes
     */
    void onOrderStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus);
    
    /**
     * Called when an order is completed
     */
    void onOrderCompleted(Order order);
    
    /**
     * Called when an order is cancelled
     */
    void onOrderCancelled(Order order);
}

