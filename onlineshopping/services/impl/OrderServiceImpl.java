package onlineshopping.services.impl;

import onlineshopping.enums.OrderStatus;
import onlineshopping.enums.PaymentMethod;
import onlineshopping.enums.ShippingMethod;
import onlineshopping.exceptions.CartException;
import onlineshopping.exceptions.OrderException;
import onlineshopping.models.*;
import onlineshopping.observers.OrderObserver;
import onlineshopping.repositories.impl.InMemoryOrderRepository;
import onlineshopping.services.*;
import onlineshopping.strategies.shipping.ShippingStrategy;
import onlineshopping.strategies.shipping.StandardShippingStrategy;
import onlineshopping.strategies.shipping.ExpressShippingStrategy;
import onlineshopping.strategies.shipping.SameDayShippingStrategy;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Implementation of OrderService
 */
public class OrderServiceImpl implements OrderService {
    
    private static final BigDecimal TAX_RATE = new BigDecimal("0.08"); // 8% tax
    
    private final InMemoryOrderRepository orderRepository;
    private final CartService cartService;
    private final InventoryService inventoryService;
    private final PaymentService paymentService;
    private final Map<ShippingMethod, ShippingStrategy> shippingStrategies;
    private final List<OrderObserver> observers = new ArrayList<>();

    public OrderServiceImpl(InMemoryOrderRepository orderRepository,
                            CartService cartService,
                            InventoryService inventoryService,
                            PaymentService paymentService) {
        this.orderRepository = orderRepository;
        this.cartService = cartService;
        this.inventoryService = inventoryService;
        this.paymentService = paymentService;
        
        // Initialize shipping strategies
        this.shippingStrategies = new EnumMap<>(ShippingMethod.class);
        shippingStrategies.put(ShippingMethod.STANDARD, new StandardShippingStrategy());
        shippingStrategies.put(ShippingMethod.EXPRESS, new ExpressShippingStrategy());
        shippingStrategies.put(ShippingMethod.SAME_DAY, new SameDayShippingStrategy());
    }

    @Override
    public Order placeOrder(String userId, Address shippingAddress, 
                            ShippingMethod shippingMethod, PaymentMethod paymentMethod) {
        // Get and validate cart
        Cart cart = cartService.getCart(userId);
        if (cart.isEmpty()) {
            throw CartException.emptyCart();
        }
        
        if (!cartService.validateCart(userId)) {
            throw new IllegalStateException("Cart validation failed - some items may be unavailable");
        }
        
        // Reserve inventory
        List<CartItem> cartItems = cart.getItems();
        for (CartItem item : cartItems) {
            if (!inventoryService.reserveStock(item.getProductId(), item.getQuantity())) {
                // Rollback any reserved items
                throw new IllegalStateException("Failed to reserve stock for: " + item.getProductId());
            }
        }
        
        try {
            // Create order items
            List<OrderItem> orderItems = new ArrayList<>();
            for (CartItem item : cartItems) {
                orderItems.add(new OrderItem(item.getProduct(), item.getQuantity()));
            }
            
            // Calculate totals
            BigDecimal subtotal = cart.getSubtotal();
            
            ShippingStrategy shippingStrategy = shippingStrategies.get(shippingMethod);
            BigDecimal shippingCost = shippingStrategy.calculateCost(shippingAddress, subtotal, 0);
            
            BigDecimal tax = subtotal.multiply(TAX_RATE).setScale(2, RoundingMode.HALF_UP);
            
            // Create order
            String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
            Order order = Order.builder()
                .id(orderId)
                .userId(userId)
                .items(orderItems)
                .shippingAddress(shippingAddress)
                .shippingMethod(shippingMethod)
                .paymentMethod(paymentMethod)
                .subtotal(subtotal)
                .shippingCost(shippingCost)
                .tax(tax)
                .build();
            
            // Process payment
            Payment payment = paymentService.processPayment(order, paymentMethod);
            if (!payment.isSuccessful() && paymentMethod.isPrepaid()) {
                // Release reserved inventory
                for (CartItem item : cartItems) {
                    inventoryService.releaseStock(item.getProductId(), item.getQuantity());
                }
                throw new IllegalStateException("Payment failed: " + payment.getFailureReason().orElse("Unknown error"));
            }
            
            order.setPaymentId(payment.getId());
            
            // Save order
            orderRepository.save(order);
            
            // Clear cart
            cartService.clearCart(userId);
            
            // Notify observers
            notifyOrderPlaced(order);
            
            return order;
            
        } catch (Exception e) {
            // Rollback inventory reservations
            for (CartItem item : cartItems) {
                try {
                    inventoryService.releaseStock(item.getProductId(), item.getQuantity());
                } catch (Exception ignored) {
                    // Best effort rollback
                }
            }
            throw e;
        }
    }

    @Override
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }

    @Override
    public List<Order> getUserOrders(String userId) {
        return orderRepository.findByUser(userId);
    }

    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }

    @Override
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
        
        OrderStatus oldStatus = order.getStatus();
        order.transitionTo(newStatus);
        orderRepository.save(order);
        
        notifyStatusChanged(order, oldStatus, newStatus);
    }

    @Override
    public void cancelOrder(String orderId, String reason) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
        
        if (!order.isCancellable()) {
            throw OrderException.cannotCancel(orderId, order.getStatus().name());
        }
        
        // Release inventory
        for (OrderItem item : order.getItems()) {
            inventoryService.releaseStock(item.getProductId(), item.getQuantity());
        }
        
        // Process refund if payment was made
        order.getPaymentId().ifPresent(paymentId -> 
            paymentService.processFullRefund(paymentId));
        
        order.cancel(reason);
        orderRepository.save(order);
        
        notifyOrderCancelled(order, reason);
    }

    @Override
    public void confirmOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
        
        OrderStatus oldStatus = order.getStatus();
        order.transitionTo(OrderStatus.CONFIRMED);
        orderRepository.save(order);
        
        // Convert reserved stock to actual deduction
        for (OrderItem item : order.getItems()) {
            inventoryService.confirmDeduction(item.getProductId(), item.getQuantity());
        }
        
        notifyStatusChanged(order, oldStatus, OrderStatus.CONFIRMED);
    }

    @Override
    public void shipOrder(String orderId, String trackingNumber) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
        
        // Ensure order is in PROCESSING status first
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            order.transitionTo(OrderStatus.PROCESSING);
        }
        
        OrderStatus oldStatus = order.getStatus();
        order.setTrackingNumber(trackingNumber);
        order.transitionTo(OrderStatus.SHIPPED);
        orderRepository.save(order);
        
        notifyOrderShipped(order, trackingNumber);
    }

    @Override
    public void markDelivered(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.notFound(orderId));
        
        // Transition through OUT_FOR_DELIVERY if needed
        if (order.getStatus() == OrderStatus.SHIPPED) {
            order.transitionTo(OrderStatus.OUT_FOR_DELIVERY);
        }
        
        order.transitionTo(OrderStatus.DELIVERED);
        orderRepository.save(order);
        
        notifyOrderDelivered(order);
    }

    @Override
    public Optional<Order> getOrderByTracking(String trackingNumber) {
        return orderRepository.findByTrackingNumber(trackingNumber);
    }

    @Override
    public List<Order> getOrdersByDateRange(LocalDateTime start, LocalDateTime end) {
        return orderRepository.findByDateRange(start, end);
    }

    @Override
    public void addObserver(OrderObserver observer) {
        observers.add(observer);
    }

    @Override
    public void removeObserver(OrderObserver observer) {
        observers.remove(observer);
    }

    private void notifyOrderPlaced(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderPlaced(order);
        }
    }

    private void notifyStatusChanged(Order order, OrderStatus from, OrderStatus to) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order, from, to);
        }
    }

    private void notifyOrderShipped(Order order, String trackingNumber) {
        for (OrderObserver observer : observers) {
            observer.onOrderShipped(order, trackingNumber);
        }
    }

    private void notifyOrderDelivered(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderDelivered(order);
        }
    }

    private void notifyOrderCancelled(Order order, String reason) {
        for (OrderObserver observer : observers) {
            observer.onOrderCancelled(order, reason);
        }
    }
}



