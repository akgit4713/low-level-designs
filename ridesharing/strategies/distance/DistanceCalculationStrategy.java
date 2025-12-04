package ridesharing.strategies.distance;

import ridesharing.models.Location;

/**
 * Strategy interface for calculating distance between two locations.
 * Follows Strategy Pattern (OCP - can add new distance algorithms).
 */
public interface DistanceCalculationStrategy {
    
    /**
     * Calculate distance between two locations in kilometers.
     */
    double calculateDistance(Location from, Location to);
    
    /**
     * Estimate travel time in minutes based on distance and average speed.
     */
    default long estimateTravelTimeMinutes(Location from, Location to) {
        double distance = calculateDistance(from, to);
        // Assume average speed of 30 km/h in city
        return Math.round((distance / 30.0) * 60);
    }
}



