package airline.enums;

/**
 * Represents the availability status of a seat.
 */
public enum SeatStatus {
    AVAILABLE("Seat is available for booking"),
    BOOKED("Seat is booked"),
    BLOCKED("Seat is temporarily blocked"),
    UNAVAILABLE("Seat is not available");

    private final String description;

    SeatStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



