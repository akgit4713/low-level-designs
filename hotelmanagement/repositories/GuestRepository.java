package hotelmanagement.repositories;

import hotelmanagement.models.Guest;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Guest entity with domain-specific queries
 */
public interface GuestRepository extends Repository<Guest, String> {
    
    /**
     * Find guest by email
     */
    Optional<Guest> findByEmail(String email);
    
    /**
     * Find guest by phone
     */
    Optional<Guest> findByPhone(String phone);
    
    /**
     * Find guest by ID document number
     */
    Optional<Guest> findByIdNumber(String idNumber);
    
    /**
     * Search guests by name (partial match)
     */
    List<Guest> searchByName(String name);
    
    /**
     * Find guests by loyalty tier
     */
    List<Guest> findByLoyaltyTier(String tier);
    
    /**
     * Find top guests by number of stays
     */
    List<Guest> findTopGuestsByStays(int limit);
}



