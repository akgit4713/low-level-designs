package concertbooking.exceptions;

/**
 * Base exception for all concert booking related errors
 */
public class ConcertBookingException extends RuntimeException {
    
    public ConcertBookingException(String message) {
        super(message);
    }
    
    public ConcertBookingException(String message, Throwable cause) {
        super(message, cause);
    }
}



