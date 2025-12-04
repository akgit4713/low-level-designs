package fooddelivery.strategies.delivery;

import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Assigns the nearest available delivery agent to the restaurant.
 */
public class NearestAgentStrategy implements DeliveryAssignmentStrategy {
    
    private final double maxDistanceKm;
    
    public NearestAgentStrategy() {
        this.maxDistanceKm = 5.0; // Default 5km radius
    }
    
    public NearestAgentStrategy(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public Optional<DeliveryAgent> assignAgent(List<DeliveryAgent> availableAgents, Location pickupLocation) {
        return availableAgents.stream()
                .filter(agent -> agent.getCurrentLocation() != null)
                .filter(agent -> agent.getCurrentLocation().distanceTo(pickupLocation) <= maxDistanceKm)
                .min(Comparator.comparingDouble(
                    agent -> agent.getCurrentLocation().distanceTo(pickupLocation)));
    }

    @Override
    public String getStrategyName() {
        return "Nearest Agent";
    }
}



