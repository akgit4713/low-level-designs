package hotelmanagement.services;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.models.Room;
import hotelmanagement.observers.RoomStatusObserver;
import hotelmanagement.strategies.pricing.PricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Service interface for room management operations
 */
public interface RoomService {
    
    // Room CRUD operations
    Room addRoom(Room room);
    Optional<Room> getRoom(String roomId);
    Optional<Room> getRoomByNumber(String roomNumber);
    List<Room> getAllRooms();
    boolean removeRoom(String roomId);
    
    // Room availability
    List<Room> getAvailableRooms();
    List<Room> getAvailableRoomsByType(RoomType type);
    boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut);
    Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut);
    
    // Room status management
    void updateRoomStatus(String roomId, RoomStatus status);
    List<Room> getRoomsByStatus(RoomStatus status);
    List<Room> getRoomsByFloor(int floor);
    
    // Pricing
    BigDecimal calculateRoomRate(Room room, LocalDate checkIn, LocalDate checkOut);
    void setPricingStrategy(PricingStrategy strategy);
    PricingStrategy getPricingStrategy();
    
    // Availability summary
    Map<RoomType, Long> getAvailabilityByType();
    long getTotalAvailableRooms();
    
    // Observers
    void addObserver(RoomStatusObserver observer);
    void removeObserver(RoomStatusObserver observer);
}



