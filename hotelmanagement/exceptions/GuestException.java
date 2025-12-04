package hotelmanagement.exceptions;

/**
 * Exception class for guest-related errors
 */
public class GuestException extends HotelException {
    
    public GuestException(String message) {
        super(message);
    }
    
    public GuestException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static GuestException guestNotFound(String guestId) {
        return new GuestException("Guest not found: " + guestId);
    }
    
    public static GuestException duplicateGuest(String email) {
        return new GuestException("Guest with email already exists: " + email);
    }
    
    public static GuestException invalidGuestData(String reason) {
        return new GuestException("Invalid guest data: " + reason);
    }
}



