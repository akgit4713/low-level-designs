package restaurant.services.impl;

import restaurant.enums.OrderStatus;
import restaurant.exceptions.OrderException;
import restaurant.models.Order;
import restaurant.observers.OrderObserver;
import restaurant.repositories.OrderRepository;
import restaurant.services.OrderService;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of OrderService with Observer pattern support
 */
public class OrderServiceImpl implements OrderService {
    
    private final OrderRepository orderRepository;
    private final List<OrderObserver> observers = new ArrayList<>();
    
    public OrderServiceImpl(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }
    
    @Override
    public Order placeOrder(Order order) {
        Order saved = orderRepository.save(order);
        notifyOrderCreated(saved);
        return saved;
    }
    
    @Override
    public Optional<Order> getOrder(String orderId) {
        return orderRepository.findById(orderId);
    }
    
    @Override
    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.orderNotFound(orderId));
        
        OrderStatus oldStatus = order.getStatus();
        order.transitionTo(newStatus);
        
        notifyStatusChanged(order, oldStatus, newStatus);
        
        if (newStatus == OrderStatus.COMPLETED) {
            notifyOrderCompleted(order);
        }
    }
    
    @Override
    public void cancelOrder(String orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> OrderException.orderNotFound(orderId));
        
        OrderStatus oldStatus = order.getStatus();
        order.transitionTo(OrderStatus.CANCELLED);
        
        notifyStatusChanged(order, oldStatus, OrderStatus.CANCELLED);
        notifyOrderCancelled(order);
    }
    
    @Override
    public List<Order> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatus(status);
    }
    
    @Override
    public List<Order> getActiveOrders() {
        return orderRepository.findActiveOrders();
    }
    
    @Override
    public List<Order> getOrdersForCustomer(String customerId) {
        return orderRepository.findByCustomerId(customerId);
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
    
    // Observer notification methods
    
    private void notifyOrderCreated(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderCreated(order);
        }
    }
    
    private void notifyStatusChanged(Order order, OrderStatus oldStatus, OrderStatus newStatus) {
        for (OrderObserver observer : observers) {
            observer.onOrderStatusChanged(order, oldStatus, newStatus);
        }
    }
    
    private void notifyOrderCompleted(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderCompleted(order);
        }
    }
    
    private void notifyOrderCancelled(Order order) {
        for (OrderObserver observer : observers) {
            observer.onOrderCancelled(order);
        }
    }
}

