package carrental.exceptions;

/**
 * Thrown when a reservation operation fails.
 */
public class ReservationException extends CarRentalException {
    
    public ReservationException(String message) {
        super(message);
    }

    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
}



