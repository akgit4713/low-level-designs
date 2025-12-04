package airline.repositories;

import airline.enums.BookingStatus;
import airline.models.Booking;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Booking entities.
 */
public interface BookingRepository extends Repository<Booking, String> {
    
    /**
     * Finds a booking by PNR.
     */
    Optional<Booking> findByPnr(String pnr);
    
    /**
     * Finds bookings for a flight.
     */
    List<Booking> findByFlightNumber(String flightNumber);
    
    /**
     * Finds bookings by status.
     */
    List<Booking> findByStatus(BookingStatus status);
    
    /**
     * Finds bookings by passenger email.
     */
    List<Booking> findByPassengerEmail(String email);
}



