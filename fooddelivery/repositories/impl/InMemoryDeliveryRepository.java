package fooddelivery.repositories.impl;

import fooddelivery.enums.DeliveryStatus;
import fooddelivery.models.Delivery;
import fooddelivery.repositories.DeliveryRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of DeliveryRepository.
 */
public class InMemoryDeliveryRepository implements DeliveryRepository {
    private final Map<String, Delivery> deliveries = new ConcurrentHashMap<>();
    private final Map<String, String> orderIdIndex = new ConcurrentHashMap<>();

    @Override
    public Delivery save(Delivery delivery) {
        deliveries.put(delivery.getId(), delivery);
        orderIdIndex.put(delivery.getOrderId(), delivery.getId());
        return delivery;
    }

    @Override
    public Optional<Delivery> findById(String id) {
        return Optional.ofNullable(deliveries.get(id));
    }

    @Override
    public Optional<Delivery> findByOrderId(String orderId) {
        String deliveryId = orderIdIndex.get(orderId);
        return deliveryId != null ? findById(deliveryId) : Optional.empty();
    }

    @Override
    public List<Delivery> findByAgentId(String agentId) {
        return deliveries.values().stream()
                .filter(d -> agentId.equals(d.getAgentId()))
                .toList();
    }

    @Override
    public List<Delivery> findByStatus(DeliveryStatus status) {
        return deliveries.values().stream()
                .filter(d -> d.getStatus() == status)
                .toList();
    }

    @Override
    public List<Delivery> findByAgentIdAndStatus(String agentId, DeliveryStatus status) {
        return deliveries.values().stream()
                .filter(d -> agentId.equals(d.getAgentId()) && d.getStatus() == status)
                .toList();
    }

    @Override
    public List<Delivery> findAll() {
        return new ArrayList<>(deliveries.values());
    }

    @Override
    public void delete(String id) {
        Delivery delivery = deliveries.remove(id);
        if (delivery != null) {
            orderIdIndex.remove(delivery.getOrderId());
        }
    }

    @Override
    public boolean existsById(String id) {
        return deliveries.containsKey(id);
    }
}



