package hotelmanagement.repositories;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.models.Room;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Room entity with domain-specific queries
 */
public interface RoomRepository extends Repository<Room, String> {
    
    /**
     * Find room by room number
     */
    Optional<Room> findByRoomNumber(String roomNumber);
    
    /**
     * Find all rooms by status
     */
    List<Room> findByStatus(RoomStatus status);
    
    /**
     * Find all available rooms
     */
    List<Room> findAvailable();
    
    /**
     * Find available rooms by type
     */
    List<Room> findAvailableByType(RoomType type);
    
    /**
     * Find all rooms on a specific floor
     */
    List<Room> findByFloor(int floor);
    
    /**
     * Find rooms by type
     */
    List<Room> findByType(RoomType type);
    
    /**
     * Count available rooms
     */
    long countAvailable();
    
    /**
     * Count available rooms by type
     */
    long countAvailableByType(RoomType type);
}



