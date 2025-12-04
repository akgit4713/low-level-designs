package concertbooking.exceptions;

/**
 * Exception thrown when a concert is not found
 */
public class ConcertNotFoundException extends ConcertBookingException {
    
    public ConcertNotFoundException(String concertId) {
        super("Concert not found with ID: " + concertId);
    }
    
    public static ConcertNotFoundException byId(String concertId) {
        return new ConcertNotFoundException(concertId);
    }
}



