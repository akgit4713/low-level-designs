package bookmyshow.repositories.impl;

import bookmyshow.enums.BookingStatus;
import bookmyshow.models.Booking;
import bookmyshow.repositories.BookingRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookingRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> bookings = new ConcurrentHashMap<>();

    @Override
    public void save(Booking booking) {
        bookings.put(booking.getId(), booking);
    }

    @Override
    public Optional<Booking> findById(String id) {
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findAll() {
        return bookings.values().stream().toList();
    }

    @Override
    public List<Booking> findByUserId(String userId) {
        return bookings.values().stream()
            .filter(b -> b.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByShowId(String showId) {
        return bookings.values().stream()
            .filter(b -> b.getShowId().equals(showId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        return bookings.values().stream()
            .filter(b -> b.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findExpiredBookings() {
        LocalDateTime now = LocalDateTime.now();
        return bookings.values().stream()
            .filter(b -> (b.getStatus() == BookingStatus.INITIATED || 
                         b.getStatus() == BookingStatus.PENDING) &&
                        now.isAfter(b.getExpiresAt()))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        bookings.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return bookings.containsKey(id);
    }
}



