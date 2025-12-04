package hotelmanagement.repositories.impl;

import hotelmanagement.models.Guest;
import hotelmanagement.repositories.GuestRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of GuestRepository
 */
public class InMemoryGuestRepository implements GuestRepository {
    
    private final ConcurrentHashMap<String, Guest> guests = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> emailIndex = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> phoneIndex = new ConcurrentHashMap<>();
    
    @Override
    public Guest save(Guest guest) {
        guests.put(guest.getId(), guest);
        emailIndex.put(guest.getEmail().toLowerCase(), guest.getId());
        phoneIndex.put(guest.getPhone(), guest.getId());
        return guest;
    }
    
    @Override
    public Optional<Guest> findById(String id) {
        return Optional.ofNullable(guests.get(id));
    }
    
    @Override
    public List<Guest> findAll() {
        return new ArrayList<>(guests.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        Guest guest = guests.remove(id);
        if (guest != null) {
            emailIndex.remove(guest.getEmail().toLowerCase());
            phoneIndex.remove(guest.getPhone());
            return true;
        }
        return false;
    }
    
    @Override
    public boolean existsById(String id) {
        return guests.containsKey(id);
    }
    
    @Override
    public long count() {
        return guests.size();
    }
    
    @Override
    public Optional<Guest> findByEmail(String email) {
        String id = emailIndex.get(email.toLowerCase());
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public Optional<Guest> findByPhone(String phone) {
        String id = phoneIndex.get(phone);
        return id != null ? findById(id) : Optional.empty();
    }
    
    @Override
    public Optional<Guest> findByIdNumber(String idNumber) {
        return guests.values().stream()
            .filter(g -> idNumber.equals(g.getIdNumber()))
            .findFirst();
    }
    
    @Override
    public List<Guest> searchByName(String name) {
        String searchTerm = name.toLowerCase();
        return guests.values().stream()
            .filter(g -> g.getName().toLowerCase().contains(searchTerm))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Guest> findByLoyaltyTier(String tier) {
        return guests.values().stream()
            .filter(g -> g.getLoyaltyTier().equals(tier))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Guest> findTopGuestsByStays(int limit) {
        return guests.values().stream()
            .sorted(Comparator.comparingInt(Guest::getTotalStays).reversed())
            .limit(limit)
            .collect(Collectors.toList());
    }
}



