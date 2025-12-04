package hotelmanagement.enums;

/**
 * Enum representing the lifecycle status of a reservation
 */
public enum ReservationStatus {
    PENDING("Reservation is pending confirmation"),
    CONFIRMED("Reservation is confirmed"),
    CHECKED_IN("Guest has checked in"),
    CHECKED_OUT("Guest has checked out"),
    CANCELLED("Reservation was cancelled"),
    NO_SHOW("Guest did not show up");

    private final String description;

    ReservationStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Check if the reservation can transition to the new status
     */
    public boolean canTransitionTo(ReservationStatus newStatus) {
        if (this == newStatus) {
            return false;
        }
        
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == CHECKED_IN || newStatus == CANCELLED || newStatus == NO_SHOW;
            case CHECKED_IN -> newStatus == CHECKED_OUT;
            case CHECKED_OUT, CANCELLED, NO_SHOW -> false; // Terminal states
        };
    }

    /**
     * Check if this is a terminal (final) state
     */
    public boolean isTerminal() {
        return this == CHECKED_OUT || this == CANCELLED || this == NO_SHOW;
    }

    /**
     * Check if this reservation is active (occupying a room)
     */
    public boolean isActive() {
        return this == CHECKED_IN;
    }
}



