package airline.enums;

/**
 * Represents the lifecycle status of a booking.
 */
public enum BookingStatus {
    PENDING("Booking is pending payment"),
    CONFIRMED("Booking is confirmed"),
    CANCELLED("Booking has been cancelled"),
    COMPLETED("Journey completed"),
    REFUNDED("Booking refunded");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    /**
     * Checks if transition to new status is valid.
     */
    public boolean canTransitionTo(BookingStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED;
            case CONFIRMED -> newStatus == CANCELLED || newStatus == COMPLETED;
            case CANCELLED -> newStatus == REFUNDED;
            case COMPLETED, REFUNDED -> false;
        };
    }
}



