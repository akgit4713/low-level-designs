package fooddelivery.strategies.delivery;

import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Assigns agents to balance workload, preferring agents with fewer deliveries
 * while still considering distance.
 */
public class LoadBalancedAgentStrategy implements DeliveryAssignmentStrategy {
    
    private final double maxDistanceKm;
    
    public LoadBalancedAgentStrategy() {
        this.maxDistanceKm = 7.0;
    }
    
    public LoadBalancedAgentStrategy(double maxDistanceKm) {
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public Optional<DeliveryAgent> assignAgent(List<DeliveryAgent> availableAgents, Location pickupLocation) {
        return availableAgents.stream()
                .filter(agent -> agent.getCurrentLocation() != null)
                .filter(agent -> agent.getCurrentLocation().distanceTo(pickupLocation) <= maxDistanceKm)
                .min(Comparator.comparingInt(DeliveryAgent::getTotalDeliveries)
                    .thenComparingDouble(agent -> 
                        agent.getCurrentLocation().distanceTo(pickupLocation)));
    }

    @Override
    public String getStrategyName() {
        return "Load Balanced";
    }
}



