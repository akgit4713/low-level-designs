package fooddelivery.services;

import fooddelivery.enums.OrderStatus;
import fooddelivery.enums.PaymentMethod;
import fooddelivery.models.Location;
import fooddelivery.models.Order;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for order operations.
 */
public interface OrderService {
    Order placeOrder(String customerId, Location deliveryAddress, PaymentMethod paymentMethod);
    Optional<Order> getOrderById(String orderId);
    List<Order> getOrdersByCustomer(String customerId);
    List<Order> getOrdersByRestaurant(String restaurantId);
    List<Order> getOrdersByStatus(OrderStatus status);
    
    // Order lifecycle
    void confirmOrder(String orderId);
    void startPreparing(String orderId);
    void markReady(String orderId);
    void markOutForDelivery(String orderId);
    void markDelivered(String orderId);
    void cancelOrder(String orderId, String reason);
    
    // Rating
    void rateOrder(String orderId, double rating, String review);
}



