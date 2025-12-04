package hotelmanagement.observers;

import hotelmanagement.models.Reservation;

/**
 * Observer interface for reservation lifecycle events
 * Implements Observer Pattern for decoupled event handling
 */
public interface ReservationObserver {
    
    /**
     * Called when a new reservation is created
     */
    void onReservationCreated(Reservation reservation);
    
    /**
     * Called when a reservation is confirmed
     */
    void onReservationConfirmed(Reservation reservation);
    
    /**
     * Called when a reservation is cancelled
     */
    void onReservationCancelled(Reservation reservation);
    
    /**
     * Called when a guest checks in
     */
    void onCheckIn(Reservation reservation);
    
    /**
     * Called when a guest checks out
     */
    void onCheckOut(Reservation reservation);
    
    /**
     * Called when a guest doesn't show up
     */
    default void onNoShow(Reservation reservation) {
        // Optional default implementation
    }
}



