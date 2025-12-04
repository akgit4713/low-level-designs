package concertbooking.services;

import concertbooking.enums.SectionType;
import concertbooking.models.WaitlistEntry;
import concertbooking.observers.WaitlistObserver;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing concert waitlists
 */
public interface WaitlistService {
    
    /**
     * Join waitlist for a concert
     * @param userId User ID
     * @param concertId Concert ID
     * @param requestedSeats Number of seats requested
     * @param preferredSection Preferred section (can be null)
     * @return Waitlist entry
     */
    WaitlistEntry joinWaitlist(String userId, String concertId, int requestedSeats, SectionType preferredSection);
    
    /**
     * Leave waitlist
     * @param userId User ID
     * @param concertId Concert ID
     */
    void leaveWaitlist(String userId, String concertId);
    
    /**
     * Get waitlist position
     * @return Position (1-based), or -1 if not on waitlist
     */
    int getWaitlistPosition(String userId, String concertId);
    
    /**
     * Get waitlist entry
     */
    Optional<WaitlistEntry> getWaitlistEntry(String userId, String concertId);
    
    /**
     * Get all waitlist entries for a concert
     */
    List<WaitlistEntry> getConcertWaitlist(String concertId);
    
    /**
     * Get user's waitlist entries
     */
    List<WaitlistEntry> getUserWaitlistEntries(String userId);
    
    /**
     * Notify waitlisted users when seats become available
     * @param concertId Concert ID
     * @param availableSeats Number of available seats
     */
    void notifyWaitlistedUsers(String concertId, int availableSeats);
    
    /**
     * Add waitlist observer
     */
    void addObserver(WaitlistObserver observer);
    
    /**
     * Remove waitlist observer
     */
    void removeObserver(WaitlistObserver observer);
}



