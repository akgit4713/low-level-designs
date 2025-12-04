package hotelmanagement.enums;

/**
 * Enum representing the current status of a hotel room
 */
public enum RoomStatus {
    AVAILABLE("Room is available for booking"),
    OCCUPIED("Room is currently occupied by a guest"),
    RESERVED("Room is reserved for an upcoming guest"),
    CLEANING("Room is being cleaned"),
    MAINTENANCE("Room is under maintenance");

    private final String description;

    RoomStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if the room can transition to the new status
     */
    public boolean canTransitionTo(RoomStatus newStatus) {
        if (this == newStatus) {
            return false;
        }
        
        return switch (this) {
            case AVAILABLE -> newStatus == OCCUPIED || newStatus == RESERVED || newStatus == MAINTENANCE;
            case OCCUPIED -> newStatus == CLEANING;
            case CLEANING -> newStatus == AVAILABLE || newStatus == MAINTENANCE;
            case RESERVED -> newStatus == OCCUPIED || newStatus == AVAILABLE;
            case MAINTENANCE -> newStatus == AVAILABLE || newStatus == CLEANING;
        };
    }
}



