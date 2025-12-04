package ridesharing.services;

import ridesharing.models.Fare;
import ridesharing.models.Location;
import ridesharing.models.RideRequest;
import ridesharing.enums.RideType;

/**
 * Service interface for fare calculation.
 */
public interface FareService {
    
    /**
     * Calculate estimated fare for a ride request.
     */
    Fare calculateEstimatedFare(RideRequest request);
    
    /**
     * Calculate fare for given distance and duration.
     */
    Fare calculateFare(double distanceKm, long durationMinutes, RideType rideType);
    
    /**
     * Calculate final fare for a completed ride.
     */
    Fare calculateFinalFare(double actualDistanceKm, long actualDurationMinutes, RideType rideType);
    
    /**
     * Get current surge multiplier for a location.
     */
    double getSurgeMultiplier(Location location);
    
    /**
     * Calculate distance between two locations.
     */
    double calculateDistance(Location from, Location to);
    
    /**
     * Estimate travel time between two locations.
     */
    long estimateTravelTime(Location from, Location to);
}



