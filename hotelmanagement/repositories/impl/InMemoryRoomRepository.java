package hotelmanagement.repositories.impl;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.models.Room;
import hotelmanagement.repositories.RoomRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of RoomRepository
 */
public class InMemoryRoomRepository implements RoomRepository {
    
    private final ConcurrentHashMap<String, Room> rooms = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> roomNumberIndex = new ConcurrentHashMap<>();
    
    @Override
    public Room save(Room room) {
        rooms.put(room.getId(), room);
        roomNumberIndex.put(room.getRoomNumber(), room.getId());
        return room;
    }
    
    @Override
    public Optional<Room> findById(String id) {
        return Optional.ofNullable(rooms.get(id));
    }
    
    @Override
    public List<Room> findAll() {
        return new ArrayList<>(rooms.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Room room = rooms.remove(id);
        if (room != null) {
            roomNumberIndex.remove(room.getRoomNumber());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return rooms.containsKey(id);
    }
    
    @Override
    public long count() {
        return rooms.size();
    }
    
    @Override
    public Optional<Room> findByRoomNumber(String roomNumber) {
        String id = roomNumberIndex.get(roomNumber);
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public List<Room> findByStatus(RoomStatus status) {
        return rooms.values().stream()
            .filter(room -> room.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Room> findAvailable() {
        return findByStatus(RoomStatus.AVAILABLE);
    }
    
    @Override
    public List<Room> findAvailableByType(RoomType type) {
        return rooms.values().stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE && room.getType() == type)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Room> findByFloor(int floor) {
        return rooms.values().stream()
            .filter(room -> room.getFloor() == floor)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Room> findByType(RoomType type) {
        return rooms.values().stream()
            .filter(room -> room.getType() == type)
            .collect(Collectors.toList());
    }
    
    @Override
    public long countAvailable() {
        return rooms.values().stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
            .count();
    }
    
    @Override
    public long countAvailableByType(RoomType type) {
        return rooms.values().stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE && room.getType() == type)
            .count();
    }
}



