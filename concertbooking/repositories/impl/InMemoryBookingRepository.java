package concertbooking.repositories.impl;

import concertbooking.enums.BookingStatus;
import concertbooking.models.Booking;
import concertbooking.repositories.BookingRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of BookingRepository
 */
public class InMemoryBookingRepository implements BookingRepository {
    
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();
    
    @Override
    public Booking save(Booking entity) {
        bookings.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }
    
    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(bookings.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return bookings.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return bookings.containsKey(id);
    }
    
    @Override
    public long count() {
        return bookings.size();
    }
    
    @Override
    public List<Booking> findByUserId(String userId) {
        return bookings.values().stream()
            .filter(b -> b.getUserId().equals(userId))
            .sorted(Comparator.comparing(Booking::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findByConcertId(String concertId) {
        return bookings.values().stream()
            .filter(b -> b.getConcertId().equals(concertId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        return bookings.values().stream()
            .filter(b -> b.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findExpiredPendingBookings() {
        LocalDateTime now = LocalDateTime.now();
        return bookings.values().stream()
            .filter(b -> b.getStatus() == BookingStatus.PENDING)
            .filter(b -> b.getExpiresAt() != null && now.isAfter(b.getExpiresAt()))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Booking> findByUserIdAndConcertId(String userId, String concertId) {
        return bookings.values().stream()
            .filter(b -> b.getUserId().equals(userId) && b.getConcertId().equals(concertId))
            .collect(Collectors.toList());
    }
}



