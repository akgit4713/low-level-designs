package hotelmanagement.exceptions;

/**
 * Base exception class for all hotel management exceptions
 */
public class HotelException extends RuntimeException {
    
    public HotelException(String message) {
        super(message);
    }
    
    public HotelException(String message, Throwable cause) {
        super(message, cause);
    }
}



