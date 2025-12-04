package concertbooking.enums;

/**
 * Represents the status of a booking
 */
public enum BookingStatus {
    PENDING("Awaiting payment"),
    CONFIRMED("Payment completed, booking confirmed"),
    CANCELLED("Booking cancelled"),
    EXPIRED("Hold expired before payment"),
    REFUNDED("Booking refunded");

    private final String description;

    BookingStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == CANCELLED || this == EXPIRED || this == REFUNDED;
    }

    public boolean canTransitionTo(BookingStatus newStatus) {
        return switch (this) {
            case PENDING -> newStatus == CONFIRMED || newStatus == CANCELLED || newStatus == EXPIRED;
            case CONFIRMED -> newStatus == CANCELLED || newStatus == REFUNDED;
            case CANCELLED, EXPIRED, REFUNDED -> false; // Terminal states
        };
    }
}



