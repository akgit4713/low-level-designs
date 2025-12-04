package carrental.observers;

import carrental.models.Reservation;

/**
 * Observer interface for reservation events.
 * Follows Observer Pattern for decoupled notification handling.
 */
public interface ReservationObserver {
    
    /**
     * Called when a new reservation is created.
     */
    void onReservationCreated(Reservation reservation);
    
    /**
     * Called when a reservation is confirmed.
     */
    void onReservationConfirmed(Reservation reservation);
    
    /**
     * Called when a reservation is modified.
     */
    void onReservationModified(Reservation reservation);
    
    /**
     * Called when a reservation is cancelled.
     */
    void onReservationCancelled(Reservation reservation);
    
    /**
     * Called when a rental is completed.
     */
    void onReservationCompleted(Reservation reservation);
}



