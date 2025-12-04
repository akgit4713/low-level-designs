package hotelmanagement.exceptions;

import hotelmanagement.enums.ReservationStatus;

/**
 * Exception class for reservation-related errors
 */
public class ReservationException extends HotelException {
    
    public ReservationException(String message) {
        super(message);
    }
    
    public ReservationException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static ReservationException reservationNotFound(String reservationId) {
        return new ReservationException("Reservation not found: " + reservationId);
    }
    
    public static ReservationException invalidStateTransition(String reservationId, 
            ReservationStatus from, ReservationStatus to) {
        return new ReservationException(String.format(
            "Invalid reservation status transition for %s: %s -> %s",
            reservationId, from, to
        ));
    }
    
    public static ReservationException invalidDateRange() {
        return new ReservationException("Check-out date must be after check-in date");
    }
    
    public static ReservationException pastCheckInDate() {
        return new ReservationException("Check-in date cannot be in the past");
    }
    
    public static ReservationException noAvailableRoom(String roomType) {
        return new ReservationException("No available room of type: " + roomType);
    }
    
    public static ReservationException alreadyCheckedIn(String reservationId) {
        return new ReservationException("Reservation " + reservationId + " is already checked in");
    }
    
    public static ReservationException notCheckedIn(String reservationId) {
        return new ReservationException("Reservation " + reservationId + " is not checked in");
    }
    
    public static ReservationException cannotModifyTerminalReservation(String reservationId) {
        return new ReservationException("Cannot modify reservation " + reservationId + " - it is in a terminal state");
    }
}



