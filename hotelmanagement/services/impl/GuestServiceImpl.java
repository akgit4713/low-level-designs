package hotelmanagement.services.impl;

import hotelmanagement.exceptions.GuestException;
import hotelmanagement.models.Guest;
import hotelmanagement.repositories.GuestRepository;
import hotelmanagement.services.GuestService;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of GuestService
 */
public class GuestServiceImpl implements GuestService {
    
    private final GuestRepository guestRepository;
    
    public GuestServiceImpl(GuestRepository guestRepository) {
        this.guestRepository = guestRepository;
    }
    
    @Override
    public Guest registerGuest(Guest guest) {
        // Check for duplicate email
        if (guestRepository.findByEmail(guest.getEmail()).isPresent()) {
            throw GuestException.duplicateGuest(guest.getEmail());
        }
        return guestRepository.save(guest);
    }
    
    @Override
    public Optional<Guest> getGuest(String guestId) {
        return guestRepository.findById(guestId);
    }
    
    @Override
    public Optional<Guest> findByEmail(String email) {
        return guestRepository.findByEmail(email);
    }
    
    @Override
    public Optional<Guest> findByPhone(String phone) {
        return guestRepository.findByPhone(phone);
    }
    
    @Override
    public List<Guest> getAllGuests() {
        return guestRepository.findAll();
    }
    
    @Override
    public Guest updateGuest(Guest guest) {
        if (!guestRepository.existsById(guest.getId())) {
            throw GuestException.guestNotFound(guest.getId());
        }
        return guestRepository.save(guest);
    }
    
    @Override
    public List<Guest> searchByName(String name) {
        return guestRepository.searchByName(name);
    }
    
    @Override
    public List<Guest> findByLoyaltyTier(String tier) {
        return guestRepository.findByLoyaltyTier(tier);
    }
    
    @Override
    public List<Guest> getTopGuests(int limit) {
        return guestRepository.findTopGuestsByStays(limit);
    }
    
    @Override
    public void addLoyaltyPoints(String guestId, int points) {
        Guest guest = guestRepository.findById(guestId)
            .orElseThrow(() -> GuestException.guestNotFound(guestId));
        guest.addLoyaltyPoints(points);
        guestRepository.save(guest);
    }
    
    @Override
    public boolean useLoyaltyPoints(String guestId, int points) {
        Guest guest = guestRepository.findById(guestId)
            .orElseThrow(() -> GuestException.guestNotFound(guestId));
        boolean success = guest.useLoyaltyPoints(points);
        if (success) {
            guestRepository.save(guest);
        }
        return success;
    }
    
    @Override
    public void incrementStayCount(String guestId) {
        Guest guest = guestRepository.findById(guestId)
            .orElseThrow(() -> GuestException.guestNotFound(guestId));
        guest.incrementStayCount();
        guestRepository.save(guest);
    }
    
    @Override
    public int getLoyaltyPoints(String guestId) {
        Guest guest = guestRepository.findById(guestId)
            .orElseThrow(() -> GuestException.guestNotFound(guestId));
        return guest.getLoyaltyPoints();
    }
    
    @Override
    public String getLoyaltyTier(String guestId) {
        Guest guest = guestRepository.findById(guestId)
            .orElseThrow(() -> GuestException.guestNotFound(guestId));
        return guest.getLoyaltyTier();
    }
}



