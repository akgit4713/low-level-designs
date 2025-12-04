package hotelmanagement.services.impl;

import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.exceptions.RoomException;
import hotelmanagement.models.Room;
import hotelmanagement.observers.RoomStatusObserver;
import hotelmanagement.repositories.ReservationRepository;
import hotelmanagement.repositories.RoomRepository;
import hotelmanagement.services.RoomService;
import hotelmanagement.strategies.pricing.PricingStrategy;
import hotelmanagement.strategies.pricing.StandardPricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * Implementation of RoomService
 */
public class RoomServiceImpl implements RoomService {
    
    private final RoomRepository roomRepository;
    private final ReservationRepository reservationRepository;
    private final List<RoomStatusObserver> observers = new CopyOnWriteArrayList<>();
    private PricingStrategy pricingStrategy;
    
    public RoomServiceImpl(RoomRepository roomRepository, ReservationRepository reservationRepository) {
        this.roomRepository = roomRepository;
        this.reservationRepository = reservationRepository;
        this.pricingStrategy = new StandardPricingStrategy();
    }
    
    @Override
    public Room addRoom(Room room) {
        if (roomRepository.findByRoomNumber(room.getRoomNumber()).isPresent()) {
            throw RoomException.duplicateRoom(room.getRoomNumber());
        }
        return roomRepository.save(room);
    }
    
    @Override
    public Optional<Room> getRoom(String roomId) {
        return roomRepository.findById(roomId);
    }
    
    @Override
    public Optional<Room> getRoomByNumber(String roomNumber) {
        return roomRepository.findByRoomNumber(roomNumber);
    }
    
    @Override
    public List<Room> getAllRooms() {
        return roomRepository.findAll();
    }
    
    @Override
    public boolean removeRoom(String roomId) {
        return roomRepository.deleteById(roomId);
    }
    
    @Override
    public List<Room> getAvailableRooms() {
        return roomRepository.findAvailable();
    }
    
    @Override
    public List<Room> getAvailableRoomsByType(RoomType type) {
        return roomRepository.findAvailableByType(type);
    }
    
    @Override
    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        Optional<Room> room = roomRepository.findById(roomId);
        if (room.isEmpty()) {
            return false;
        }
        
        // Check if room is in available status
        if (room.get().getStatus() != RoomStatus.AVAILABLE) {
            return false;
        }
        
        // Check for overlapping reservations
        return reservationRepository.isRoomAvailable(roomId, checkIn, checkOut);
    }
    
    @Override
    public Optional<Room> findAvailableRoom(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        List<Room> roomsOfType = roomRepository.findByType(type);
        
        return roomsOfType.stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
            .filter(room -> reservationRepository.isRoomAvailable(room.getId(), checkIn, checkOut))
            .findFirst();
    }
    
    @Override
    public void updateRoomStatus(String roomId, RoomStatus status) {
        Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> RoomException.roomNotFound(roomId));
        
        RoomStatus oldStatus = room.getStatus();
        room.transitionTo(status);
        roomRepository.save(room);
        
        // Notify observers
        notifyStatusChange(room, oldStatus, status);
    }
    
    @Override
    public List<Room> getRoomsByStatus(RoomStatus status) {
        return roomRepository.findByStatus(status);
    }
    
    @Override
    public List<Room> getRoomsByFloor(int floor) {
        return roomRepository.findByFloor(floor);
    }
    
    @Override
    public BigDecimal calculateRoomRate(Room room, LocalDate checkIn, LocalDate checkOut) {
        return pricingStrategy.calculateTotalRate(room, checkIn, checkOut);
    }
    
    @Override
    public void setPricingStrategy(PricingStrategy strategy) {
        this.pricingStrategy = strategy;
    }
    
    @Override
    public PricingStrategy getPricingStrategy() {
        return pricingStrategy;
    }
    
    @Override
    public Map<RoomType, Long> getAvailabilityByType() {
        return Arrays.stream(RoomType.values())
            .collect(Collectors.toMap(
                type -> type,
                type -> roomRepository.countAvailableByType(type)
            ));
    }
    
    @Override
    public long getTotalAvailableRooms() {
        return roomRepository.countAvailable();
    }
    
    @Override
    public void addObserver(RoomStatusObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(RoomStatusObserver observer) {
        observers.remove(observer);
    }
    
    private void notifyStatusChange(Room room, RoomStatus oldStatus, RoomStatus newStatus) {
        for (RoomStatusObserver observer : observers) {
            observer.onRoomStatusChanged(room, oldStatus, newStatus);
            
            if (newStatus == RoomStatus.CLEANING) {
                observer.onRoomNeedsCleaning(room);
            } else if (newStatus == RoomStatus.AVAILABLE && oldStatus == RoomStatus.CLEANING) {
                observer.onRoomCleaned(room);
            }
        }
    }
}



