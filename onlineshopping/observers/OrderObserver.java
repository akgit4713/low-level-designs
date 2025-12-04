package onlineshopping.observers;

import onlineshopping.enums.OrderStatus;
import onlineshopping.models.Order;

/**
 * Observer interface for order lifecycle events
 */
public interface OrderObserver {
    
    /**
     * Called when a new order is placed
     */
    void onOrderPlaced(Order order);
    
    /**
     * Called when order status changes
     */
    void onOrderStatusChanged(Order order, OrderStatus from, OrderStatus to);
    
    /**
     * Called when order is shipped
     */
    void onOrderShipped(Order order, String trackingNumber);
    
    /**
     * Called when order is delivered
     */
    void onOrderDelivered(Order order);
    
    /**
     * Called when order is cancelled
     */
    void onOrderCancelled(Order order, String reason);
}



