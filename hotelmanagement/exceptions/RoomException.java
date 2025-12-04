package hotelmanagement.exceptions;

import hotelmanagement.enums.RoomStatus;

/**
 * Exception class for room-related errors
 */
public class RoomException extends HotelException {
    
    public RoomException(String message) {
        super(message);
    }
    
    public RoomException(String message, Throwable cause) {
        super(message, cause);
    }
    
    public static RoomException roomNotFound(String roomId) {
        return new RoomException("Room not found: " + roomId);
    }
    
    public static RoomException roomNotAvailable(String roomNumber) {
        return new RoomException("Room " + roomNumber + " is not available for booking");
    }
    
    public static RoomException invalidStateTransition(String roomNumber, RoomStatus from, RoomStatus to) {
        return new RoomException(String.format(
            "Invalid room status transition for room %s: %s -> %s",
            roomNumber, from, to
        ));
    }
    
    public static RoomException duplicateRoom(String roomNumber) {
        return new RoomException("Room already exists: " + roomNumber);
    }
    
    public static RoomException capacityExceeded(String roomNumber, int capacity, int requested) {
        return new RoomException(String.format(
            "Room %s capacity exceeded: max %d, requested %d",
            roomNumber, capacity, requested
        ));
    }
}



