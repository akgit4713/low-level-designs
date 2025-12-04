package hotelmanagement.services.impl;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.enums.RoomStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.enums.ServiceType;
import hotelmanagement.exceptions.ReservationException;
import hotelmanagement.exceptions.RoomException;
import hotelmanagement.models.Guest;
import hotelmanagement.models.Reservation;
import hotelmanagement.models.Room;
import hotelmanagement.models.ServiceCharge;
import hotelmanagement.observers.ReservationObserver;
import hotelmanagement.repositories.ReservationRepository;
import hotelmanagement.repositories.RoomRepository;
import hotelmanagement.services.ReservationService;
import hotelmanagement.strategies.pricing.PricingStrategy;
import hotelmanagement.strategies.pricing.StandardPricingStrategy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of ReservationService
 */
public class ReservationServiceImpl implements ReservationService {
    
    private final ReservationRepository reservationRepository;
    private final RoomRepository roomRepository;
    private final List<ReservationObserver> observers = new CopyOnWriteArrayList<>();
    private PricingStrategy pricingStrategy;
    
    public ReservationServiceImpl(ReservationRepository reservationRepository, 
                                   RoomRepository roomRepository) {
        this.reservationRepository = reservationRepository;
        this.roomRepository = roomRepository;
        this.pricingStrategy = new StandardPricingStrategy();
    }
    
    public void setPricingStrategy(PricingStrategy pricingStrategy) {
        this.pricingStrategy = pricingStrategy;
    }
    
    @Override
    public Reservation createReservation(Guest guest, Room room, LocalDate checkIn, 
                                          LocalDate checkOut, int numberOfGuests) {
        // Validate dates
        if (checkIn.isBefore(LocalDate.now())) {
            throw ReservationException.pastCheckInDate();
        }
        if (!checkOut.isAfter(checkIn)) {
            throw ReservationException.invalidDateRange();
        }
        
        // Check room capacity
        if (!room.canAccommodate(numberOfGuests)) {
            throw RoomException.capacityExceeded(room.getRoomNumber(), room.getCapacity(), numberOfGuests);
        }
        
        // Check room availability
        if (!reservationRepository.isRoomAvailable(room.getId(), checkIn, checkOut)) {
            throw RoomException.roomNotAvailable(room.getRoomNumber());
        }
        
        // Calculate room rate
        BigDecimal roomRate = pricingStrategy.calculateRate(room, checkIn);
        
        // Create reservation
        Reservation reservation = Reservation.builder()
            .guest(guest)
            .room(room)
            .checkInDate(checkIn)
            .checkOutDate(checkOut)
            .numberOfGuests(numberOfGuests)
            .roomRatePerNight(roomRate)
            .build();
        
        // Reserve the room
        room.tryReserve();
        roomRepository.save(room);
        
        // Save reservation
        Reservation saved = reservationRepository.save(reservation);
        
        // Notify observers
        notifyCreated(saved);
        
        return saved;
    }
    
    @Override
    public Optional<Reservation> getReservation(String reservationId) {
        return reservationRepository.findById(reservationId);
    }
    
    @Override
    public List<Reservation> getAllReservations() {
        return reservationRepository.findAll();
    }
    
    @Override
    public List<Reservation> getReservationsByGuest(String guestId) {
        return reservationRepository.findByGuestId(guestId);
    }
    
    @Override
    public List<Reservation> getReservationsByStatus(ReservationStatus status) {
        return reservationRepository.findByStatus(status);
    }
    
    @Override
    public void confirmReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        reservation.confirm();
        reservationRepository.save(reservation);
        
        notifyConfirmed(reservation);
    }
    
    @Override
    public void cancelReservation(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        // Release the room
        Room room = reservation.getRoom();
        room.release();
        roomRepository.save(room);
        
        reservation.cancel();
        reservationRepository.save(reservation);
        
        notifyCancelled(reservation);
    }
    
    @Override
    public void markNoShow(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        // Release the room
        Room room = reservation.getRoom();
        room.release();
        roomRepository.save(room);
        
        reservation.markNoShow();
        reservationRepository.save(reservation);
        
        notifyNoShow(reservation);
    }
    
    @Override
    public Optional<Room> findAndBookRoom(RoomType type, LocalDate checkIn, 
                                           LocalDate checkOut, int numberOfGuests) {
        List<Room> availableRooms = searchAvailableRooms(type, checkIn, checkOut);
        
        return availableRooms.stream()
            .filter(room -> room.canAccommodate(numberOfGuests))
            .findFirst();
    }
    
    @Override
    public List<Room> searchAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut) {
        return roomRepository.findByType(type).stream()
            .filter(room -> room.getStatus() == RoomStatus.AVAILABLE)
            .filter(room -> reservationRepository.isRoomAvailable(room.getId(), checkIn, checkOut))
            .toList();
    }
    
    @Override
    public void addServiceCharge(String reservationId, ServiceType type, 
                                  BigDecimal amount, String description) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        if (reservation.getStatus() != ReservationStatus.CHECKED_IN) {
            throw ReservationException.notCheckedIn(reservationId);
        }
        
        ServiceCharge charge = new ServiceCharge(type, amount, description);
        reservation.addServiceCharge(charge);
        reservationRepository.save(reservation);
    }
    
    @Override
    public List<ServiceCharge> getServiceCharges(String reservationId) {
        Reservation reservation = reservationRepository.findById(reservationId)
            .orElseThrow(() -> ReservationException.reservationNotFound(reservationId));
        
        return reservation.getServiceCharges();
    }
    
    @Override
    public List<Reservation> getTodayCheckIns() {
        return reservationRepository.findByCheckInDate(LocalDate.now()).stream()
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
            .toList();
    }
    
    @Override
    public List<Reservation> getTodayCheckOuts() {
        return reservationRepository.findByCheckOutDate(LocalDate.now()).stream()
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
            .toList();
    }
    
    @Override
    public List<Reservation> getActiveReservations() {
        return reservationRepository.findActiveReservations();
    }
    
    @Override
    public List<Reservation> getUpcomingReservations() {
        return reservationRepository.findUpcomingReservations();
    }
    
    @Override
    public void addObserver(ReservationObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(ReservationObserver observer) {
        observers.remove(observer);
    }
    
    // Observer notification methods
    private void notifyCreated(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationCreated(reservation);
        }
    }
    
    private void notifyConfirmed(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationConfirmed(reservation);
        }
    }
    
    private void notifyCancelled(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onReservationCancelled(reservation);
        }
    }
    
    private void notifyCheckIn(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onCheckIn(reservation);
        }
    }
    
    private void notifyCheckOut(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onCheckOut(reservation);
        }
    }
    
    private void notifyNoShow(Reservation reservation) {
        for (ReservationObserver observer : observers) {
            observer.onNoShow(reservation);
        }
    }
}



