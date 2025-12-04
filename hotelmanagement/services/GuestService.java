package hotelmanagement.services;

import hotelmanagement.models.Guest;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for guest management operations
 */
public interface GuestService {
    
    // Guest CRUD
    Guest registerGuest(Guest guest);
    Optional<Guest> getGuest(String guestId);
    Optional<Guest> findByEmail(String email);
    Optional<Guest> findByPhone(String phone);
    List<Guest> getAllGuests();
    Guest updateGuest(Guest guest);
    
    // Guest search
    List<Guest> searchByName(String name);
    List<Guest> findByLoyaltyTier(String tier);
    List<Guest> getTopGuests(int limit);
    
    // Loyalty management
    void addLoyaltyPoints(String guestId, int points);
    boolean useLoyaltyPoints(String guestId, int points);
    void incrementStayCount(String guestId);
    int getLoyaltyPoints(String guestId);
    String getLoyaltyTier(String guestId);
}



