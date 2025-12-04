package concertbooking.enums;

/**
 * Represents the status of a seat in a concert venue
 */
public enum SeatStatus {
    AVAILABLE("Available for booking"),
    HELD("Temporarily held for a user"),
    BOOKED("Permanently booked"),
    BLOCKED("Blocked by venue - not for sale");

    private final String description;

    SeatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if transition to new status is valid
     */
    public boolean canTransitionTo(SeatStatus newStatus) {
        return switch (this) {
            case AVAILABLE -> newStatus == HELD || newStatus == BLOCKED;
            case HELD -> newStatus == BOOKED || newStatus == AVAILABLE;
            case BOOKED -> newStatus == AVAILABLE; // Only for cancellation/refund
            case BLOCKED -> newStatus == AVAILABLE;
        };
    }
}



