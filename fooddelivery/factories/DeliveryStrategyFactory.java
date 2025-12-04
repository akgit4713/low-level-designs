package fooddelivery.factories;

import fooddelivery.strategies.delivery.*;

/**
 * Factory for creating delivery assignment strategy instances.
 */
public class DeliveryStrategyFactory {
    
    public enum AssignmentType {
        NEAREST,
        HIGHEST_RATED,
        LOAD_BALANCED
    }
    
    public DeliveryAssignmentStrategy createStrategy(AssignmentType type) {
        return switch (type) {
            case NEAREST -> new NearestAgentStrategy();
            case HIGHEST_RATED -> new HighestRatedAgentStrategy();
            case LOAD_BALANCED -> new LoadBalancedAgentStrategy();
        };
    }
    
    public DeliveryAssignmentStrategy createCustomNearestStrategy(double maxDistanceKm) {
        return new NearestAgentStrategy(maxDistanceKm);
    }
    
    public DeliveryAssignmentStrategy createCustomRatedStrategy(double maxDistanceKm, double minRating) {
        return new HighestRatedAgentStrategy(maxDistanceKm, minRating);
    }
}



