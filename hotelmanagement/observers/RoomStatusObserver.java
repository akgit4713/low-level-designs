package hotelmanagement.observers;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.models.Room;

/**
 * Observer interface for room status changes
 * Used for housekeeping notifications and display updates
 */
public interface RoomStatusObserver {
    
    /**
     * Called when room status changes
     */
    void onRoomStatusChanged(Room room, RoomStatus oldStatus, RoomStatus newStatus);
    
    /**
     * Called when room needs cleaning
     */
    void onRoomNeedsCleaning(Room room);
    
    /**
     * Called when room cleaning is completed
     */
    void onRoomCleaned(Room room);
    
    /**
     * Called when room requires maintenance
     */
    default void onMaintenanceRequired(Room room, String issue) {
        // Optional default implementation
    }
}



