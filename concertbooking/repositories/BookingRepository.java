package concertbooking.repositories;

import concertbooking.enums.BookingStatus;
import concertbooking.models.Booking;

import java.util.List;

/**
 * Repository interface for Booking entity
 */
public interface BookingRepository extends Repository<Booking, String> {
    
    List<Booking> findByUserId(String userId);
    
    List<Booking> findByConcertId(String concertId);
    
    List<Booking> findByStatus(BookingStatus status);
    
    List<Booking> findExpiredPendingBookings();
    
    List<Booking> findByUserIdAndConcertId(String userId, String concertId);
}



