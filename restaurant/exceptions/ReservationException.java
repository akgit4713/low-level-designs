package restaurant.exceptions;

/**
 * Exception for reservation-related errors
 */
public class ReservationException extends RestaurantException {

    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }

    public static ReservationException tableNotAvailable(String tableId, String timeSlot) {
        return new ReservationException(
            String.format("Table %s is not available at %s", tableId, timeSlot)
        );
    }

    public static ReservationException reservationNotFound(String reservationId) {
        return new ReservationException("Reservation not found: " + reservationId);
    }

    public static ReservationException invalidPartySize(int partySize, int tableCapacity) {
        return new ReservationException(
            String.format("Party size %d exceeds table capacity %d", partySize, tableCapacity)
        );
    }
}

