package fooddelivery.strategies.delivery;

import fooddelivery.models.DeliveryAgent;
import fooddelivery.models.Location;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Assigns the highest-rated available delivery agent within range.
 * Useful for premium customers or high-value orders.
 */
public class HighestRatedAgentStrategy implements DeliveryAssignmentStrategy {
    
    private final double maxDistanceKm;
    private final double minRating;
    
    public HighestRatedAgentStrategy() {
        this.maxDistanceKm = 8.0;
        this.minRating = 4.0;
    }
    
    public HighestRatedAgentStrategy(double maxDistanceKm, double minRating) {
        this.maxDistanceKm = maxDistanceKm;
        this.minRating = minRating;
    }

    @Override
    public Optional<DeliveryAgent> assignAgent(List<DeliveryAgent> availableAgents, Location pickupLocation) {
        return availableAgents.stream()
                .filter(agent -> agent.getCurrentLocation() != null)
                .filter(agent -> agent.getCurrentLocation().distanceTo(pickupLocation) <= maxDistanceKm)
                .filter(agent -> agent.getRating() >= minRating)
                .max(Comparator.comparingDouble(DeliveryAgent::getRating));
    }

    @Override
    public String getStrategyName() {
        return "Highest Rated Agent";
    }
}



