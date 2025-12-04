package trafficsignal.enums;

/**
 * Types of emergency vehicles that can trigger signal override.
 */
public enum EmergencyType {
    AMBULANCE("Ambulance", 1),
    FIRE_TRUCK("Fire Truck", 1),
    POLICE("Police", 2),
    VIP_CONVOY("VIP Convoy", 3);

    private final String displayName;
    private final int priorityLevel; // Lower is higher priority

    EmergencyType(String displayName, int priorityLevel) {
        this.displayName = displayName;
        this.priorityLevel = priorityLevel;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getPriorityLevel() {
        return priorityLevel;
    }
}



