package hotelmanagement.repositories;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.models.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Reservation entity with domain-specific queries
 */
public interface ReservationRepository extends Repository<Reservation, String> {
    
    /**
     * Find reservations by guest ID
     */
    List<Reservation> findByGuestId(String guestId);
    
    /**
     * Find reservations by room ID
     */
    List<Reservation> findByRoomId(String roomId);
    
    /**
     * Find reservations by status
     */
    List<Reservation> findByStatus(ReservationStatus status);
    
    /**
     * Find reservations with check-in date on a specific date
     */
    List<Reservation> findByCheckInDate(LocalDate date);
    
    /**
     * Find reservations with check-out date on a specific date
     */
    List<Reservation> findByCheckOutDate(LocalDate date);
    
    /**
     * Find active reservations (checked-in guests)
     */
    List<Reservation> findActiveReservations();
    
    /**
     * Find reservations overlapping with date range
     */
    List<Reservation> findOverlappingReservations(String roomId, LocalDate checkIn, LocalDate checkOut);
    
    /**
     * Check if room is available for given date range
     */
    boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut);
    
    /**
     * Find upcoming reservations (confirmed, check-in in future)
     */
    List<Reservation> findUpcomingReservations();
}



