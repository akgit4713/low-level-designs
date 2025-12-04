package hotelmanagement.services;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.enums.RoomType;
import hotelmanagement.enums.ServiceType;
import hotelmanagement.models.Guest;
import hotelmanagement.models.Reservation;
import hotelmanagement.models.Room;
import hotelmanagement.models.ServiceCharge;
import hotelmanagement.observers.ReservationObserver;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for reservation management operations
 */
public interface ReservationService {
    
    // Reservation CRUD
    Reservation createReservation(Guest guest, Room room, LocalDate checkIn, LocalDate checkOut, int numberOfGuests);
    Optional<Reservation> getReservation(String reservationId);
    List<Reservation> getAllReservations();
    List<Reservation> getReservationsByGuest(String guestId);
    List<Reservation> getReservationsByStatus(ReservationStatus status);
    
    // Reservation lifecycle
    void confirmReservation(String reservationId);
    void cancelReservation(String reservationId);
    void markNoShow(String reservationId);
    
    // Room search and booking
    Optional<Room> findAndBookRoom(RoomType type, LocalDate checkIn, LocalDate checkOut, int numberOfGuests);
    List<Room> searchAvailableRooms(RoomType type, LocalDate checkIn, LocalDate checkOut);
    
    // Service charges
    void addServiceCharge(String reservationId, ServiceType type, BigDecimal amount, String description);
    List<ServiceCharge> getServiceCharges(String reservationId);
    
    // Queries
    List<Reservation> getTodayCheckIns();
    List<Reservation> getTodayCheckOuts();
    List<Reservation> getActiveReservations();
    List<Reservation> getUpcomingReservations();
    
    // Observers
    void addObserver(ReservationObserver observer);
    void removeObserver(ReservationObserver observer);
}



