package concertbooking.repositories;

import concertbooking.enums.ConcertStatus;
import concertbooking.models.Concert;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for Concert entity
 */
public interface ConcertRepository extends Repository<Concert, String> {
    
    List<Concert> findByStatus(ConcertStatus status);
    
    List<Concert> findByArtist(String artist);
    
    List<Concert> findByVenueCity(String city);
    
    List<Concert> findByDateRange(LocalDateTime start, LocalDateTime end);
    
    List<Concert> findUpcoming();
    
    List<Concert> findBookable();
}



