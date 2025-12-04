package concertbooking.observers;

import concertbooking.models.WaitlistEntry;

/**
 * Observer interface for waitlist events
 */
public interface WaitlistObserver {
    
    /**
     * Called when a user joins the waitlist
     */
    void onWaitlistJoined(WaitlistEntry entry);
    
    /**
     * Called when seats become available for a waitlisted user
     */
    void onSeatsAvailable(WaitlistEntry entry, int availableSeats);
    
    /**
     * Called when a user is removed from waitlist
     */
    void onWaitlistRemoved(WaitlistEntry entry);
}



