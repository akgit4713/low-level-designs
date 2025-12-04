package concertbooking.exceptions;

import java.util.List;

/**
 * Exception thrown when seats are not available for booking
 */
public class SeatNotAvailableException extends ConcertBookingException {
    
    private final List<String> seatIds;
    
    public SeatNotAvailableException(String message, List<String> seatIds) {
        super(message);
        this.seatIds = seatIds;
    }
    
    public List<String> getSeatIds() {
        return seatIds;
    }
    
    public static SeatNotAvailableException seatsAlreadyBooked(List<String> seatIds) {
        return new SeatNotAvailableException(
            "The following seats are no longer available: " + seatIds, 
            seatIds
        );
    }
    
    public static SeatNotAvailableException seatsHeldByAnother(List<String> seatIds) {
        return new SeatNotAvailableException(
            "The following seats are currently held by another user: " + seatIds, 
            seatIds
        );
    }
}



