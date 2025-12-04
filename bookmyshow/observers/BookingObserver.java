package bookmyshow.observers;

import bookmyshow.models.Booking;

/**
 * Observer interface for booking events.
 * Implementations can handle notifications, analytics, etc.
 */
public interface BookingObserver {
    
    /**
     * Called when a booking is confirmed.
     * @param booking The confirmed booking
     */
    void onBookingConfirmed(Booking booking);
    
    /**
     * Called when a booking is cancelled.
     * @param booking The cancelled booking
     */
    void onBookingCancelled(Booking booking);
    
    /**
     * Called when a booking expires.
     * @param booking The expired booking
     */
    void onBookingExpired(Booking booking);
}



