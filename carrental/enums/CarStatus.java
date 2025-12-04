package carrental.enums;

/**
 * Represents the current status of a car in the system.
 */
public enum CarStatus {
    AVAILABLE("Available for rental"),
    RESERVED("Currently reserved"),
    RENTED("Currently rented out"),
    UNDER_MAINTENANCE("Under maintenance"),
    OUT_OF_SERVICE("Out of service");

    private final String description;

    CarStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



