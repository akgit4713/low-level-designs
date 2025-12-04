package parkinglot.models;

import parkinglot.enums.VehicleType;

import java.util.Map;

/**
 * Represents a display board at entry points showing real-time availability.
 */
public class DisplayBoard {
    private final String displayId;
    private Map<Integer, Map<VehicleType, Integer>> availability;

    public DisplayBoard(String displayId) {
        this.displayId = displayId;
    }

    public String getDisplayId() {
        return displayId;
    }

    /**
     * Updates the display with current availability.
     * 
     * @param availability Map of level number to vehicle type availability
     */
    public void updateAvailability(Map<Integer, Map<VehicleType, Integer>> availability) {
        this.availability = availability;
        render();
    }

    /**
     * Renders the display output.
     */
    public void render() {
        StringBuilder sb = new StringBuilder();
        sb.append("\n┌────────────────────────────────────────┐\n");
        sb.append("│     PARKING AVAILABILITY [").append(displayId).append("]     │\n");
        sb.append("├────────────────────────────────────────┤\n");
        
        if (availability != null) {
            for (Map.Entry<Integer, Map<VehicleType, Integer>> entry : availability.entrySet()) {
                int level = entry.getKey();
                Map<VehicleType, Integer> spots = entry.getValue();
                sb.append(String.format("│  Level %d: 🏍️ %-3d  🚗 %-3d  🚚 %-3d     │%n",
                    level,
                    spots.getOrDefault(VehicleType.MOTORCYCLE, 0),
                    spots.getOrDefault(VehicleType.CAR, 0),
                    spots.getOrDefault(VehicleType.TRUCK, 0)));
            }
        }
        
        sb.append("└────────────────────────────────────────┘");
        System.out.println(sb);
    }

    /**
     * Shows a custom message on the display.
     */
    public void showMessage(String message) {
        System.out.println("[" + displayId + "] " + message);
    }
}



