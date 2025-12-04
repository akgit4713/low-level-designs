package fooddelivery.strategies.delivery;

import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for assigning delivery agents to orders.
 * Implements Strategy Pattern for different assignment algorithms.
 */
public interface DeliveryAssignmentStrategy {
    
    /**
     * Find the best available delivery agent for the given pickup location.
     * @param availableAgents List of available delivery agents
     * @param pickupLocation Restaurant location for pickup
     * @return Optional containing the selected agent, or empty if none suitable
     */
    Optional<DeliveryAgent> assignAgent(List<DeliveryAgent> availableAgents, Location pickupLocation);
    
    /**
     * Get the strategy name for logging/display purposes.
     */
    String getStrategyName();
}



