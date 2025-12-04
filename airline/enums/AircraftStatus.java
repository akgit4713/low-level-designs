package airline.enums;

/**
 * Operational status of an aircraft.
 */
public enum AircraftStatus {
    AVAILABLE("Available for assignment"),
    IN_FLIGHT("Currently in flight"),
    MAINTENANCE("Under maintenance"),
    RETIRED("Retired from service");

    private final String description;

    AircraftStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



