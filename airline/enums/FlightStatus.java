package airline.enums;

/**
 * Represents the operational status of a flight.
 */
public enum FlightStatus {
    SCHEDULED("Flight is scheduled"),
    BOARDING("Boarding in progress"),
    DEPARTED("Flight has departed"),
    IN_AIR("Flight is in the air"),
    LANDED("Flight has landed"),
    DELAYED("Flight is delayed"),
    CANCELLED("Flight is cancelled");

    private final String description;

    FlightStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if transition to new status is valid.
     */
    public boolean canTransitionTo(FlightStatus newStatus) {
        return switch (this) {
            case SCHEDULED -> newStatus == BOARDING || newStatus == DELAYED || newStatus == CANCELLED;
            case BOARDING -> newStatus == DEPARTED || newStatus == DELAYED || newStatus == CANCELLED;
            case DEPARTED -> newStatus == IN_AIR;
            case IN_AIR -> newStatus == LANDED;
            case DELAYED -> newStatus == BOARDING || newStatus == CANCELLED;
            case LANDED, CANCELLED -> false;
        };
    }
}



