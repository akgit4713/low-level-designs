package concertbooking.observers;

import concertbooking.models.Booking;

/**
 * Observer interface for booking events
 */
public interface BookingObserver {
    
    /**
     * Called when a booking is created (pending payment)
     */
    void onBookingCreated(Booking booking);
    
    /**
     * Called when a booking is confirmed (payment successful)
     */
    void onBookingConfirmed(Booking booking);
    
    /**
     * Called when a booking is cancelled
     */
    void onBookingCancelled(Booking booking);
    
    /**
     * Called when a booking expires
     */
    void onBookingExpired(Booking booking);
}



