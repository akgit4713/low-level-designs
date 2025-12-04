package carrental.enums;

/**
 * Represents the status of a reservation.
 */
public enum ReservationStatus {
    PENDING("Reservation pending confirmation"),
    CONFIRMED("Reservation confirmed"),
    ACTIVE("Car currently rented"),
    COMPLETED("Rental completed"),
    CANCELLED("Reservation cancelled");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean canModify() {
        return this == PENDING || this == CONFIRMED;
    }

    public boolean canCancel() {
        return this == PENDING || this == CONFIRMED;
    }
}



