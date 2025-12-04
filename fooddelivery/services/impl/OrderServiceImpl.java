package fooddelivery.services.impl;

import fooddelivery.enums.NotificationType;
import fooddelivery.enums.OrderStatus;
import fooddelivery.enums.PaymentMethod;
import fooddelivery.exceptions.CartException;
import fooddelivery.exceptions.OrderException;
import fooddelivery.models.*;
import fooddelivery.observers.OrderEvent;
import fooddelivery.observers.OrderEventPublisher;
import fooddelivery.repositories.CartRepository;
import fooddelivery.repositories.OrderRepository;
import fooddelivery.repositories.RestaurantRepository;
import fooddelivery.services.DeliveryService;
import fooddelivery.services.OrderService;
import fooddelivery.services.PaymentService;
import fooddelivery.strategies.pricing.DeliveryFeeStrategy;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Implementation of OrderService with event publishing.
 */
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final RestaurantRepository restaurantRepository;
    private final PaymentService paymentService;
    private final DeliveryService deliveryService;
    private final DeliveryFeeStrategy deliveryFeeStrategy;
    private final OrderEventPublisher eventPublisher;
    
    public OrderServiceImpl(OrderRepository orderRepository,
                            CartRepository cartRepository,
                            RestaurantRepository restaurantRepository,
                            PaymentService paymentService,
                            DeliveryService deliveryService,
                            DeliveryFeeStrategy deliveryFeeStrategy,
                            OrderEventPublisher eventPublisher) {
        this.orderRepository = orderRepository;
        this.cartRepository = cartRepository;
        this.restaurantRepository = restaurantRepository;
        this.paymentService = paymentService;
        this.deliveryService = deliveryService;
        this.deliveryFeeStrategy = deliveryFeeStrategy;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Order placeOrder(String customerId, Location deliveryAddress, PaymentMethod paymentMethod) {
        // Get cart and validate
        Cart cart = cartRepository.findByCustomerId(customerId)
                .orElseThrow(() -> new CartException("Cart not found"));
        
        if (cart.isEmpty()) {
            throw new OrderException("Cannot place order with empty cart");
        }
        
        // Get restaurant and validate
        Restaurant restaurant = restaurantRepository.findById(cart.getRestaurantId())
                .orElseThrow(() -> new OrderException("Restaurant not found"));
        
        if (!restaurant.isAcceptingOrders()) {
            throw new OrderException("Restaurant is not accepting orders");
        }
        
        // Check minimum order amount
        if (cart.getSubtotal().doubleValue() < restaurant.getMinimumOrderAmount()) {
            throw new OrderException("Order amount below minimum: " + restaurant.getMinimumOrderAmount());
        }
        
        // Create order
        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8);
        Order order = new Order(orderId, customerId, restaurant.getId(), 
                               cart.getItems(), deliveryAddress);
        order.setPaymentMethod(paymentMethod);
        
        // Calculate delivery fee
        var deliveryFee = deliveryFeeStrategy.calculateFee(
            order, restaurant.getLocation(), deliveryAddress);
        order.setDeliveryFee(deliveryFee);
        
        // Process payment
        Payment payment = paymentService.initiatePayment(order, paymentMethod);
        order.setPaymentId(payment.getId());
        
        boolean paymentSuccess = paymentService.processPayment(payment.getId());
        if (!paymentSuccess) {
            publishEvent(NotificationType.PAYMENT_FAILED, order, "Payment failed");
            throw new OrderException("Payment failed. Please try again.");
        }
        
        // Create delivery
        Delivery delivery = deliveryService.createDelivery(order, restaurant.getLocation());
        order.setDeliveryId(delivery.getId());
        
        // Save order and clear cart
        orderRepository.save(order);
        cart.clear();
        cartRepository.save(cart);
        
        // Publish events
        publishEvent(NotificationType.ORDER_PLACED, order, "Order placed successfully");
        publishEvent(NotificationType.PAYMENT_SUCCESS, order, "Payment completed");
        
        return order;
    }

    @Override
    public Optional<Order> getOrderById(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getOrdersByCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
    }

    @Override
    public List<Order> getOrdersByRestaurant(String restaurantId) {
        return orderRepository.findByRestaurantId(restaurantId);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void confirmOrder(String orderId) {
        Order order = getOrderOrThrow(orderId);
        order.confirm();
        orderRepository.save(order);
        
        // Try to assign delivery agent
        deliveryService.assignDeliveryAgent(order.getDeliveryId());
        
        publishEvent(NotificationType.ORDER_CONFIRMED, order, "Order confirmed by restaurant");
    }

    @Override
    public void startPreparing(String orderId) {
        Order order = getOrderOrThrow(orderId);
        order.startPreparing();
        orderRepository.save(order);
        
        publishEvent(NotificationType.ORDER_PREPARING, order, "Order is being prepared");
    }

    @Override
    public void markReady(String orderId) {
        Order order = getOrderOrThrow(orderId);
        order.markReady();
        orderRepository.save(order);
        
        publishEvent(NotificationType.ORDER_READY, order, "Order is ready for pickup");
    }

    @Override
    public void markOutForDelivery(String orderId) {
        Order order = getOrderOrThrow(orderId);
        order.markOutForDelivery();
        orderRepository.save(order);
        
        deliveryService.pickupOrder(order.getDeliveryId());
        
        publishEvent(NotificationType.ORDER_PICKED_UP, order, "Order is on the way");
    }

    @Override
    public void markDelivered(String orderId) {
        Order order = getOrderOrThrow(orderId);
        order.markDelivered();
        orderRepository.save(order);
        
        deliveryService.completeDelivery(order.getDeliveryId());
        
        publishEvent(NotificationType.ORDER_DELIVERED, order, "Order delivered successfully");
    }

    @Override
    public void cancelOrder(String orderId, String reason) {
        Order order = getOrderOrThrow(orderId);
        
        if (!order.canBeCancelled()) {
            throw new OrderException("Order cannot be cancelled in current state: " + order.getStatus());
        }
        
        order.cancel(reason);
        orderRepository.save(order);
        
        // Process refund
        paymentService.refundPayment(orderId);
        
        publishEvent(NotificationType.ORDER_CANCELLED, order, "Order cancelled: " + reason);
    }

    @Override
    public void rateOrder(String orderId, double rating, String review) {
        Order order = getOrderOrThrow(orderId);
        order.addRating(rating, review);
        orderRepository.save(order);
    }
    
    private Order getOrderOrThrow(String orderId) {
        return orderRepository.findById(orderId)
                .orElseThrow(() -> new OrderException("Order not found: " + orderId));
    }
    
    private void publishEvent(NotificationType type, Order order, String message) {
        eventPublisher.notifyObservers(new OrderEvent(type, order, message));
    }
}



