package fooddelivery.repositories.impl;

import fooddelivery.enums.AgentStatus;
import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import fooddelivery.repositories.DeliveryAgentRepository;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of DeliveryAgentRepository.
 */
public class InMemoryDeliveryAgentRepository implements DeliveryAgentRepository {
    private final Map<String, DeliveryAgent> agents = new ConcurrentHashMap<>();

    @Override
    public DeliveryAgent save(DeliveryAgent agent) {
        agents.put(agent.getId(), agent);
        return agent;
    }

    @Override
    public Optional<DeliveryAgent> findById(String id) {
        return Optional.ofNullable(agents.get(id));
    }

    @Override
    public List<DeliveryAgent> findByStatus(AgentStatus status) {
        return agents.values().stream()
                .filter(a -> a.getStatus() == status)
                .toList();
    }

    @Override
    public List<DeliveryAgent> findAvailable() {
        return agents.values().stream()
                .filter(DeliveryAgent::isAvailable)
                .toList();
    }

    @Override
    public List<DeliveryAgent> findNearLocation(Location location, double radiusKm) {
        return agents.values().stream()
                .filter(DeliveryAgent::isAvailable)
                .filter(a -> a.getCurrentLocation() != null)
                .filter(a -> a.getCurrentLocation().distanceTo(location) <= radiusKm)
                .sorted(Comparator.comparingDouble(
                    a -> a.getCurrentLocation().distanceTo(location)))
                .toList();
    }

    @Override
    public List<DeliveryAgent> findAll() {
        return new ArrayList<>(agents.values());
    }

    @Override
    public void delete(String id) {
        agents.remove(id);
    }

    @Override
    public boolean existsById(String id) {
        return agents.containsKey(id);
    }
}



