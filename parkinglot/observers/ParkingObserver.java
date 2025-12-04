package parkinglot.observers;

import parkinglot.models.ParkingTicket;

/**
 * Observer interface for parking lot events.
 * Implements the Observer Pattern for decoupled event handling.
 * 
 * Extension point: Implement for display boards, notifications,
 * analytics, logging, etc.
 */
public interface ParkingObserver {
    
    /**
     * Called when a vehicle is successfully parked.
     * 
     * @param ticket The ticket issued for the parked vehicle
     */
    void onVehicleParked(ParkingTicket ticket);
    
    /**
     * Called when a vehicle exits the parking lot.
     * 
     * @param ticket The ticket of the exiting vehicle
     */
    void onVehicleUnparked(ParkingTicket ticket);
}



