package concertbooking.observers;

import concertbooking.models.WaitlistEntry;

/**
 * Observer that sends notifications for waitlist events
 */
public class WaitlistNotificationObserver implements WaitlistObserver {
    
    @Override
    public void onWaitlistJoined(WaitlistEntry entry) {
        System.out.println("[WAITLIST] User " + entry.getUserId() 
            + " joined waitlist for concert " + entry.getConcertId()
            + " - Requested seats: " + entry.getRequestedSeats());
    }
    
    @Override
    public void onSeatsAvailable(WaitlistEntry entry, int availableSeats) {
        System.out.println("[WAITLIST ALERT] Seats now available for concert " + entry.getConcertId() 
            + "! User " + entry.getUserId() + " has been notified."
            + " Available: " + availableSeats + ", Requested: " + entry.getRequestedSeats());
    }
    
    @Override
    public void onWaitlistRemoved(WaitlistEntry entry) {
        System.out.println("[WAITLIST] User " + entry.getUserId() 
            + " removed from waitlist for concert " + entry.getConcertId());
    }
}



