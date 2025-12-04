package fooddelivery.repositories;

import fooddelivery.enums.DeliveryStatus;
import fooddelivery.models.Delivery;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Delivery entities.
 */
public interface DeliveryRepository {
    Delivery save(Delivery delivery);
    Optional<Delivery> findById(String id);
    Optional<Delivery> findByOrderId(String orderId);
    List<Delivery> findByAgentId(String agentId);
    List<Delivery> findByStatus(DeliveryStatus status);
    List<Delivery> findByAgentIdAndStatus(String agentId, DeliveryStatus status);
    List<Delivery> findAll();
    void delete(String id);
    boolean existsById(String id);
}



