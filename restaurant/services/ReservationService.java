package restaurant.services;

import restaurant.models.Reservation;
import restaurant.models.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for reservation management
 */
public interface ReservationService {
    
    /**
     * Make a new reservation
     */
    Reservation makeReservation(String customerName, String customerPhone, 
                                 Table table, LocalDateTime dateTime, 
                                 int partySize, String specialRequests);
    
    /**
     * Get reservation by ID
     */
    Optional<Reservation> getReservation(String reservationId);
    
    /**
     * Cancel a reservation
     */
    void cancelReservation(String reservationId);
    
    /**
     * Check in a reservation
     */
    void checkInReservation(String reservationId);
    
    /**
     * Find available tables for a given party size and time
     */
    List<Table> findAvailableTables(LocalDateTime dateTime, int partySize, int durationMinutes);
    
    /**
     * Get reservations for a date
     */
    List<Reservation> getReservationsForDate(LocalDate date);
    
    /**
     * Get upcoming reservations
     */
    List<Reservation> getUpcomingReservations(int hoursAhead);
    
    /**
     * Check if table is available at given time
     */
    boolean isTableAvailable(Table table, LocalDateTime dateTime, int durationMinutes);
}

