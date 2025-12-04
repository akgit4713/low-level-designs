package fooddelivery.repositories.impl;

import fooddelivery.enums.OrderStatus;
import fooddelivery.models.Order;
import fooddelivery.repositories.OrderRepository;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of OrderRepository.
 */
public class InMemoryOrderRepository implements OrderRepository {
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
    public List<Order> findByCustomerId(String customerId) {
        return orders.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Order> findByRestaurantId(String restaurantId) {
        return orders.values().stream()
                .filter(o -> o.getRestaurantId().equals(restaurantId))
                .sorted(Comparator.comparing(Order::getCreatedAt).reversed())
                .toList();
    }

    @Override
    public List<Order> findByStatus(OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getStatus() == status)
                .toList();
    }

    @Override
    public List<Order> findByCustomerIdAndStatus(String customerId, OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getCustomerId().equals(customerId) && o.getStatus() == status)
                .toList();
    }

    @Override
    public List<Order> findByRestaurantIdAndStatus(String restaurantId, OrderStatus status) {
        return orders.values().stream()
                .filter(o -> o.getRestaurantId().equals(restaurantId) && o.getStatus() == status)
                .toList();
    }

    @Override
    public List<Order> findByCreatedAtBetween(LocalDateTime start, LocalDateTime end) {
        return orders.values().stream()
                .filter(o -> !o.getCreatedAt().isBefore(start) && !o.getCreatedAt().isAfter(end))
                .sorted(Comparator.comparing(Order::getCreatedAt))
                .toList();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(orders.values());
    }

    @Override
    public void delete(String id) {
        orders.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return orders.containsKey(id);
    }
}



