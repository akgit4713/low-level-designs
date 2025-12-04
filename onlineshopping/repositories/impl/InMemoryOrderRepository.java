package onlineshopping.repositories.impl;

import onlineshopping.enums.OrderStatus;
import onlineshopping.models.Order;
import onlineshopping.repositories.Repository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of order repository
 */
public class InMemoryOrderRepository implements Repository<Order, String> {
    
    private final Map<String, Order> orders = new ConcurrentHashMap<>();

    @Override
    public Order save(Order order) {
        orders.put(order.getId(), order);
        return order;
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

    /**
     * Find orders by user
     */
    public List<Order> findByUser(String userId) {
        return orders.values().stream()
            .filter(o -> o.getUserId().equals(userId))
            .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Find orders by status
     */
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
            .filter(o -> o.getStatus() == status)
            .collect(Collectors.toList());
    }

    /**
     * Find orders within date range
     */
    public List<Order> findByDateRange(LocalDateTime start, LocalDateTime end) {
        return orders.values().stream()
            .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
            .sorted(Comparator.comparing(Order::getCreatedAt))
            .collect(Collectors.toList());
    }

    /**
     * Find active (non-terminal) orders
     */
    public List<Order> findActive() {
        return orders.values().stream()
            .filter(o -> !o.getStatus().isTerminal())
            .collect(Collectors.toList());
    }

    /**
     * Find orders by tracking number
     */
    public Optional<Order> findByTrackingNumber(String trackingNumber) {
        return orders.values().stream()
            .filter(o -> o.getTrackingNumber().map(t -> t.equals(trackingNumber)).orElse(false))
            .findFirst();
    }
}



