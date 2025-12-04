package ridesharing.services;

import ridesharing.models.Location;
import ridesharing.models.Ride;

import java.util.Optional;

/**
 * Service interface for real-time ride tracking.
 */
public interface TrackingService {
    
    /**
     * Update driver location.
     */
    void updateDriverLocation(String driverId, Location location);
    
    /**
     * Get current location of a ride.
     */
    Optional<Location> getRideLocation(String rideId);
    
    /**
     * Get ETA to destination.
     */
    long getETAMinutes(String rideId);
    
    /**
     * Get distance remaining to destination.
     */
    double getRemainingDistance(String rideId);
    
    /**
     * Start tracking a ride.
     */
    void startTracking(Ride ride);
    
    /**
     * Stop tracking a ride.
     */
    void stopTracking(String rideId);
}



