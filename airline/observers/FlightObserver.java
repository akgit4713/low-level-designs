package airline.observers;

import airline.enums.FlightStatus;
import airline.models.Flight;

/**
 * Observer interface for flight-related events.
 */
public interface FlightObserver {
    
    /**
     * Called when a flight status changes.
     */
    void onFlightStatusChanged(Flight flight, FlightStatus oldStatus, FlightStatus newStatus);
    
    /**
     * Called when a flight is delayed.
     */
    void onFlightDelayed(Flight flight, String reason);
    
    /**
     * Called when a flight is cancelled.
     */
    void onFlightCancelled(Flight flight, String reason);
}



