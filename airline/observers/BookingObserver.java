package airline.observers;

import airline.models.Booking;

/**
 * Observer interface for booking-related events.
 */
public interface BookingObserver {
    
    /**
     * Called when a booking is created.
     */
    void onBookingCreated(Booking booking);
    
    /**
     * Called when a booking is confirmed.
     */
    void onBookingConfirmed(Booking booking);
    
    /**
     * Called when a booking is cancelled.
     */
    void onBookingCancelled(Booking booking);
}



