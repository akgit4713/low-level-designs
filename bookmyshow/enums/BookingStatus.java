package bookmyshow.enums;

/**
 * Status of a booking throughout its lifecycle.
 */
public enum BookingStatus {
    INITIATED,      // Booking process started, seats locked
    PENDING,        // Awaiting payment
    CONFIRMED,      // Payment successful, booking confirmed
    CANCELLED,      // Booking cancelled by user
    EXPIRED,        // Booking expired (payment timeout)
    REFUNDED        // Booking refunded
}



