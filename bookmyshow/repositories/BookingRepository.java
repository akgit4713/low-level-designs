package bookmyshow.repositories;

import bookmyshow.enums.BookingStatus;
import bookmyshow.models.Booking;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Booking entity operations.
 */
public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findById(String id);
    List<Booking> findAll();
    List<Booking> findByUserId(String userId);
    List<Booking> findByShowId(String showId);
    List<Booking> findByStatus(BookingStatus status);
    List<Booking> findExpiredBookings();
    void delete(String id);
    boolean exists(String id);
}



