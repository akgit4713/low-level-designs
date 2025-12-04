package concertbooking.exceptions;

/**
 * Exception for waitlist-related errors
 */
public class WaitlistException extends ConcertBookingException {
    
    public WaitlistException(String message) {
        super(message);
    }
    
    public static WaitlistException alreadyOnWaitlist(String userId, String concertId) {
        return new WaitlistException(
            String.format("User %s is already on the waitlist for concert %s", userId, concertId)
        );
    }
    
    public static WaitlistException notOnWaitlist(String userId, String concertId) {
        return new WaitlistException(
            String.format("User %s is not on the waitlist for concert %s", userId, concertId)
        );
    }
    
    public static WaitlistException concertNotSoldOut(String concertId) {
        return new WaitlistException(
            "Concert is not sold out, waitlist not available: " + concertId
        );
    }
}



