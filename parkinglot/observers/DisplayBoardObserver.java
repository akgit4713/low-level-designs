package parkinglot.observers;

import parkinglot.models.ParkingTicket;

/**
 * Observer that updates display boards when parking events occur.
 */
public class DisplayBoardObserver implements ParkingObserver {
    
    private final String displayId;

    public DisplayBoardObserver(String displayId) {
        this.displayId = displayId;
    }

    @Override
    public void onVehicleParked(ParkingTicket ticket) {
        System.out.println("  📺 [" + displayId + "] Display updated: Vehicle parked at Level " + 
            ticket.getLevel().getFloorNumber() + ", Spot " + ticket.getParkingSpot().getSpotNumber());
    }

    @Override
    public void onVehicleUnparked(ParkingTicket ticket) {
        System.out.println("  📺 [" + displayId + "] Display updated: Spot " + 
            ticket.getParkingSpot().getSpotNumber() + " now available on Level " + 
            ticket.getLevel().getFloorNumber());
    }

    public String getDisplayId() {
        return displayId;
    }
}



