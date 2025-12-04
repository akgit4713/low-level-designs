package parkinglot.models;

import parkinglot.enums.VehicleType;

import java.util.Map;

/**
 * Represents an entry gate in the parking lot.
 * Handles vehicle entry and displays availability information.
 */
public class EntryGate {
    private final String gateId;
    private final DisplayBoard displayBoard;

    public EntryGate(String gateId) {
        this.gateId = gateId;
        this.displayBoard = new DisplayBoard("DISPLAY-" + gateId);
    }

    public String getGateId() {
        return gateId;
    }

    public DisplayBoard getDisplayBoard() {
        return displayBoard;
    }

    /**
     * Updates the display board with current availability.
     */
    public void updateDisplay(Map<Integer, Map<VehicleType, Integer>> availability) {
        displayBoard.updateAvailability(availability);
    }

    /**
     * Shows entry confirmation message.
     */
    public void showWelcome(Vehicle vehicle) {
        System.out.println("🚧 Gate " + gateId + ": Welcome " + vehicle.getLicensePlate() + "!");
    }

    @Override
    public String toString() {
        return String.format("EntryGate[%s]", gateId);
    }
}



