package restaurant.repositories.impl;

import restaurant.enums.OrderStatus;
import restaurant.models.Order;
import restaurant.repositories.OrderRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of OrderRepository
 */
public class InMemoryOrderRepository implements OrderRepository {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();
    
    @Override
    public Order save(Order entity) {
        orders.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Order> findById(String id) {
        return Optional.ofNullable(orders.get(id));
    }
    
    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return orders.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return orders.containsKey(id);
    }
    
    @Override
    public long count() {
        return orders.size();
    }
    
    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
            .filter(order -> order.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
            .filter(order -> order.getCustomerId().equals(customerId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByTableId(String tableId) {
        return orders.values().stream()
            .filter(order -> order.getTable().isPresent() && 
                            order.getTable().get().getId().equals(tableId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return orders.values().stream()
            .filter(order -> !order.getCreatedAt().isBefore(start) && 
                            !order.getCreatedAt().isAfter(end))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Order> findActiveOrders() {
        return orders.values().stream()
            .filter(order -> order.getStatus() != OrderStatus.COMPLETED && 
                            order.getStatus() != OrderStatus.CANCELLED)
            .collect(Collectors.toList());
    }
}

