package bookmyshow.exceptions;

/**
 * Thrown when requested seats are not available for booking.
 */
public class SeatNotAvailableException extends BookMyShowException {
    
    public SeatNotAvailableException(String message) {
        super(message);
    }
    
    public SeatNotAvailableException(String seatId, String showId) {
        super(String.format("Seat %s is not available for show %s", seatId, showId));
    }
}



