package ridesharing.observers;

import ridesharing.models.Ride;

/**
 * Observer interface for ride status updates.
 * Follows Observer Pattern for decoupled notifications.
 */
public interface RideObserver {
    
    /**
     * Called when a ride status changes.
     *
     * @param ride The ride that was updated
     */
    void onRideStatusChanged(Ride ride);
    
    /**
     * Called when a ride location is updated.
     *
     * @param ride The ride being tracked
     */
    void onLocationUpdated(Ride ride);
    
    /**
     * Called when a driver is matched with a ride.
     *
     * @param ride The ride that was matched
     */
    void onDriverMatched(Ride ride);
}



