package ridesharing.services;

import ridesharing.models.Ride;
import ridesharing.observers.RideObserver;

/**
 * Service interface for sending notifications.
 */
public interface NotificationService {
    
    /**
     * Register an observer for ride updates.
     */
    void registerObserver(RideObserver observer);
    
    /**
     * Remove an observer.
     */
    void removeObserver(RideObserver observer);
    
    /**
     * Notify all observers of a ride status change.
     */
    void notifyRideStatusChanged(Ride ride);
    
    /**
     * Notify all observers of a location update.
     */
    void notifyLocationUpdated(Ride ride);
    
    /**
     * Notify all observers of a driver match.
     */
    void notifyDriverMatched(Ride ride);
}



