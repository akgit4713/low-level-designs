package carrental.exceptions;

/**
 * Thrown when a reservation cannot be found in the system.
 */
public class ReservationNotFoundException extends CarRentalException {
    
    public ReservationNotFoundException(String reservationId) {
        super("Reservation not found with ID: " + reservationId);
    }
}



