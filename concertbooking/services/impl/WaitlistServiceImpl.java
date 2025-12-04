package concertbooking.services.impl;

import concertbooking.enums.ConcertStatus;
import concertbooking.enums.SectionType;
import concertbooking.exceptions.ConcertNotFoundException;
import concertbooking.exceptions.WaitlistException;
import concertbooking.models.Concert;
import concertbooking.models.WaitlistEntry;
import concertbooking.observers.WaitlistObserver;
import concertbooking.repositories.ConcertRepository;
import concertbooking.repositories.WaitlistRepository;
import concertbooking.services.WaitlistService;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Implementation of WaitlistService
 */
public class WaitlistServiceImpl implements WaitlistService {
    
    private static final int WAITLIST_NOTIFICATION_VALIDITY_MINUTES = 30;
    
    private final WaitlistRepository waitlistRepository;
    private final ConcertRepository concertRepository;
    private final List<WaitlistObserver> observers = new CopyOnWriteArrayList<>();
    
    public WaitlistServiceImpl(WaitlistRepository waitlistRepository, 
                               ConcertRepository concertRepository) {
        this.waitlistRepository = waitlistRepository;
        this.concertRepository = concertRepository;
    }
    
    @Override
    public WaitlistEntry joinWaitlist(String userId, String concertId, 
                                       int requestedSeats, SectionType preferredSection) {
        // Check if concert exists
        Concert concert = concertRepository.findById(concertId)
            .orElseThrow(() -> ConcertNotFoundException.byId(concertId));
        
        // Check if concert allows waitlist
        if (!concert.getStatus().canJoinWaitlist() && concert.getStatus() != ConcertStatus.ON_SALE) {
            throw WaitlistException.concertNotSoldOut(concertId);
        }
        
        // Check if user is already on waitlist
        if (waitlistRepository.findByUserIdAndConcertId(userId, concertId).isPresent()) {
            throw WaitlistException.alreadyOnWaitlist(userId, concertId);
        }
        
        // Create waitlist entry
        String entryId = "WL-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        WaitlistEntry entry = new WaitlistEntry(entryId, concertId, userId, requestedSeats, preferredSection);
        
        waitlistRepository.save(entry);
        
        // Notify observers
        observers.forEach(o -> o.onWaitlistJoined(entry));
        
        System.out.println("[WAITLIST] User " + userId + " joined waitlist for concert " 
            + concertId + " (position: " + getWaitlistPosition(userId, concertId) + ")");
        
        return entry;
    }
    
    @Override
    public void leaveWaitlist(String userId, String concertId) {
        WaitlistEntry entry = waitlistRepository.findByUserIdAndConcertId(userId, concertId)
            .orElseThrow(() -> WaitlistException.notOnWaitlist(userId, concertId));
        
        waitlistRepository.deleteById(entry.getId());
        
        // Notify observers
        observers.forEach(o -> o.onWaitlistRemoved(entry));
        
        System.out.println("[WAITLIST] User " + userId + " left waitlist for concert " + concertId);
    }
    
    @Override
    public int getWaitlistPosition(String userId, String concertId) {
        List<WaitlistEntry> waitlist = waitlistRepository.findByConcertIdOrderedByCreatedAt(concertId);
        
        for (int i = 0; i < waitlist.size(); i++) {
            if (waitlist.get(i).getUserId().equals(userId)) {
                return i + 1; // 1-based position
            }
        }
        
        return -1; // Not on waitlist
    }
    
    @Override
    public Optional<WaitlistEntry> getWaitlistEntry(String userId, String concertId) {
        return waitlistRepository.findByUserIdAndConcertId(userId, concertId);
    }
    
    @Override
    public List<WaitlistEntry> getConcertWaitlist(String concertId) {
        return waitlistRepository.findByConcertIdOrderedByCreatedAt(concertId);
    }
    
    @Override
    public List<WaitlistEntry> getUserWaitlistEntries(String userId) {
        return waitlistRepository.findByUserId(userId);
    }
    
    @Override
    public void notifyWaitlistedUsers(String concertId, int availableSeats) {
        if (availableSeats <= 0) return;
        
        List<WaitlistEntry> unnotifiedEntries = waitlistRepository.findUnnotifiedByConcertId(concertId);
        
        int seatsToAllocate = availableSeats;
        
        for (WaitlistEntry entry : unnotifiedEntries) {
            if (seatsToAllocate <= 0) break;
            
            if (entry.getRequestedSeats() <= seatsToAllocate) {
                entry.markNotified(WAITLIST_NOTIFICATION_VALIDITY_MINUTES);
                waitlistRepository.save(entry);
                
                // Notify observers
                observers.forEach(o -> o.onSeatsAvailable(entry, availableSeats));
                
                seatsToAllocate -= entry.getRequestedSeats();
            }
        }
    }
    
    @Override
    public void addObserver(WaitlistObserver observer) {
        observers.add(observer);
    }
    
    @Override
    public void removeObserver(WaitlistObserver observer) {
        observers.remove(observer);
    }
}



