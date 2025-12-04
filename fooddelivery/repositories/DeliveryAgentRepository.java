package fooddelivery.repositories;

import fooddelivery.enums.AgentStatus;
import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for DeliveryAgent entities.
 */
public interface DeliveryAgentRepository {
    DeliveryAgent save(DeliveryAgent agent);
    Optional<DeliveryAgent> findById(String id);
    List<DeliveryAgent> findByStatus(AgentStatus status);
    List<DeliveryAgent> findAvailable();
    List<DeliveryAgent> findNearLocation(Location location, double radiusKm);
    List<DeliveryAgent> findAll();
    void delete(String id);
    boolean existsById(String id);
}



