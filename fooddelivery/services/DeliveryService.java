package fooddelivery.services;

import fooddelivery.enums.AgentStatus;
import fooddelivery.models.Delivery;
import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import fooddelivery.models.Order;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for delivery operations.
 */
public interface DeliveryService {
    // Agent management
    DeliveryAgent registerAgent(String name, String email, String phone, String vehicleNumber);
    Optional<DeliveryAgent> getAgentById(String agentId);
    List<DeliveryAgent> getAvailableAgents();
    void updateAgentStatus(String agentId, AgentStatus status);
    void updateAgentLocation(String agentId, Location location);
    
    // Delivery operations
    Delivery createDelivery(Order order, Location restaurantLocation);
    boolean assignDeliveryAgent(String deliveryId);
    void pickupOrder(String deliveryId);
    void completeDelivery(String deliveryId);
    void updateDeliveryLocation(String deliveryId, Location location);
    Optional<Delivery> getDeliveryByOrderId(String orderId);
    Optional<Delivery> trackDelivery(String deliveryId);
    
    // Agent rating
    void rateAgent(String agentId, double rating);
}



