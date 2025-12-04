package concertbooking.exceptions;

/**
 * Exception for booking-related errors
 */
public class BookingException extends ConcertBookingException {
    
    public BookingException(String message) {
        super(message);
    }
    
    public BookingException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static BookingException notFound(String bookingId) {
        return new BookingException("Booking not found with ID: " + bookingId);
    }
    
    public static BookingException expired(String bookingId) {
        return new BookingException("Booking has expired: " + bookingId);
    }
    
    public static BookingException alreadyConfirmed(String bookingId) {
        return new BookingException("Booking is already confirmed: " + bookingId);
    }
    
    public static BookingException alreadyCancelled(String bookingId) {
        return new BookingException("Booking is already cancelled: " + bookingId);
    }
    
    public static BookingException concertNotBookable(String concertId) {
        return new BookingException("Concert is not available for booking: " + concertId);
    }
    
    public static BookingException invalidSeats() {
        return new BookingException("At least one seat must be selected");
    }
    
    public static BookingException maxSeatsExceeded(int max) {
        return new BookingException("Cannot book more than " + max + " seats in a single booking");
    }
}



