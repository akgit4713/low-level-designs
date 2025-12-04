package carrental.services;

import carrental.models.Reservation;

import java.time.LocalDate;
import java.util.List;

/**
 * Service interface for reservation management operations.
 */
public interface ReservationService {
    
    /**
     * Creates a new reservation.
     */
    Reservation createReservation(String customerId, String carId, LocalDate startDate, LocalDate endDate);
    
    /**
     * Gets a reservation by its ID.
     */
    Reservation getReservationById(String reservationId);
    
    /**
     * Gets all reservations for a customer.
     */
    List<Reservation> getReservationsByCustomer(String customerId);
    
    /**
     * Gets all reservations for a car.
     */
    List<Reservation> getReservationsByCar(String carId);
    
    /**
     * Modifies reservation dates.
     */
    Reservation modifyReservation(String reservationId, LocalDate newStartDate, LocalDate newEndDate);
    
    /**
     * Confirms a pending reservation.
     */
    Reservation confirmReservation(String reservationId);
    
    /**
     * Cancels a reservation.
     */
    Reservation cancelReservation(String reservationId);
    
    /**
     * Marks a reservation as active (car picked up).
     */
    Reservation startRental(String reservationId);
    
    /**
     * Completes a reservation (car returned).
     */
    Reservation completeReservation(String reservationId);
    
    /**
     * Gets all reservations.
     */
    List<Reservation> getAllReservations();
}



