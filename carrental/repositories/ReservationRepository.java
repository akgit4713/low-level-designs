package carrental.repositories;

import carrental.enums.ReservationStatus;
import carrental.models.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * Repository interface for Reservation entities.
 */
public interface ReservationRepository extends Repository<Reservation, String> {
    
    /**
     * Finds all reservations for a specific customer.
     */
    List<Reservation> findByCustomerId(String customerId);
    
    /**
     * Finds all reservations for a specific car.
     */
    List<Reservation> findByCarId(String carId);
    
    /**
     * Finds all reservations with a specific status.
     */
    List<Reservation> findByStatus(ReservationStatus status);
    
    /**
     * Finds reservations for a car within a date range.
     * Used to check for conflicts.
     */
    List<Reservation> findByCarIdAndDateRange(String carId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds active or confirmed reservations for a car within a date range.
     */
    List<Reservation> findActiveByCarIdAndDateRange(String carId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Finds all reservations starting on a specific date.
     */
    List<Reservation> findByStartDate(LocalDate startDate);
    
    /**
     * Finds all reservations ending on a specific date.
     */
    List<Reservation> findByEndDate(LocalDate endDate);
}



