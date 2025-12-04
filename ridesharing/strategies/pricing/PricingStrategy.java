package ridesharing.strategies.pricing;

import ridesharing.enums.RideType;
import ridesharing.models.Fare;

/**
 * Strategy interface for calculating ride fares.
 * Follows Strategy Pattern (OCP - can add new pricing strategies).
 */
public interface PricingStrategy {
    
    /**
     * Calculate fare based on distance, duration, and ride type.
     *
     * @param distanceKm Distance in kilometers
     * @param durationMinutes Duration in minutes
     * @param rideType Type of ride
     * @return Calculated fare breakdown
     */
    Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType);
    
    /**
     * Get the name of this pricing strategy.
     */
    String getStrategyName();
}



