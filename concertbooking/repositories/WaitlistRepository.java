package concertbooking.repositories;

import concertbooking.models.WaitlistEntry;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for WaitlistEntry entity
 */
public interface WaitlistRepository extends Repository<WaitlistEntry, String> {
    
    List<WaitlistEntry> findByConcertId(String concertId);
    
    List<WaitlistEntry> findByUserId(String userId);
    
    Optional<WaitlistEntry> findByUserIdAndConcertId(String userId, String concertId);
    
    /**
     * Get waitlist entries ordered by creation time (FIFO)
     */
    List<WaitlistEntry> findByConcertIdOrderedByCreatedAt(String concertId);
    
    /**
     * Get entries that haven't been notified yet
     */
    List<WaitlistEntry> findUnnotifiedByConcertId(String concertId);
}



