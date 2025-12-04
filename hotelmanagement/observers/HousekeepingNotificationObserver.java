package hotelmanagement.observers;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.models.Room;

/**
 * Observer that notifies housekeeping staff about room status changes
 */
public class HousekeepingNotificationObserver implements RoomStatusObserver {
    
    @Override
    public void onRoomStatusChanged(Room room, RoomStatus oldStatus, RoomStatus newStatus) {
        System.out.printf("\n🏠 HOUSEKEEPING ALERT: Room %s status changed: %s -> %s%n",
            room.getRoomNumber(), oldStatus, newStatus);
    }
    
    @Override
    public void onRoomNeedsCleaning(Room room) {
        System.out.printf("\n🧹 HOUSEKEEPING TASK: Room %s (Floor %d) needs cleaning%n",
            room.getRoomNumber(), room.getFloor());
        System.out.println("   Priority: HIGH - Guest checked out");
        System.out.println("   Please clean and prepare for next guest.");
    }
    
    @Override
    public void onRoomCleaned(Room room) {
        System.out.printf("\n✅ HOUSEKEEPING COMPLETE: Room %s is now clean and available%n",
            room.getRoomNumber());
    }
    
    @Override
    public void onMaintenanceRequired(Room room, String issue) {
        System.out.printf("\n🔧 MAINTENANCE REQUIRED: Room %s%n", room.getRoomNumber());
        System.out.println("   Issue: " + issue);
        System.out.println("   Status: Room marked for maintenance");
    }
}



