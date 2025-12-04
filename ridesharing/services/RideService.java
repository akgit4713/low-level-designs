package ridesharing.services;

import ridesharing.models.Fare;
import ridesharing.models.Ride;
import ridesharing.models.RideRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for ride management.
 */
public interface RideService {
    
    /**
     * Create a new ride from a ride request.
     */
    Ride requestRide(RideRequest request);
    
    /**
     * Accept a ride (driver action).
     */
    void acceptRide(String rideId, String driverId);
    
    /**
     * Mark driver as arrived at pickup.
     */
    void driverArrived(String rideId);
    
    /**
     * Start the ride.
     */
    void startRide(String rideId);
    
    /**
     * Complete the ride.
     */
    Ride completeRide(String rideId);
    
    /**
     * Cancel a ride.
     */
    void cancelRide(String rideId, String reason);
    
    /**
     * Get ride by ID.
     */
    Optional<Ride> getRide(String rideId);
    
    /**
     * Get active rides for a passenger.
     */
    List<Ride> getActiveRidesForPassenger(String passengerId);
    
    /**
     * Get ride history for a passenger.
     */
    List<Ride> getRideHistoryForPassenger(String passengerId);
    
    /**
     * Get active ride for a driver.
     */
    Optional<Ride> getActiveRideForDriver(String driverId);
    
    /**
     * Get estimated fare for a ride request.
     */
    Fare estimateFare(RideRequest request);
    
    /**
     * Rate a completed ride.
     */
    void rateDriver(String rideId, int rating);
    
    void ratePassenger(String rideId, int rating);
    
    /**
     * Update ride location (for tracking).
     */
    void updateRideLocation(String rideId, ridesharing.models.Location location);
}



