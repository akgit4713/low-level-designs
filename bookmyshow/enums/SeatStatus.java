package bookmyshow.enums;

/**
 * Status of a seat for a particular show.
 */
public enum SeatStatus {
    AVAILABLE,      // Seat is available for booking
    LOCKED,         // Temporarily locked during booking process
    BOOKED,         // Seat has been booked
    UNAVAILABLE     // Seat is not available (maintenance, blocked)
}



