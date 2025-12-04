package restaurant.repositories.impl;

import restaurant.models.Reservation;
import restaurant.repositories.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory repository for reservations
 */
public class InMemoryReservationRepository implements Repository<Reservation, String> {
    
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();
    
    @Override
    public Reservation save(Reservation entity) {
        reservations.put(entity.getId(), entity);
        return entity;
    }
    
    @Override
    public Optional<Reservation> findById(String id) {
        return Optional.ofNullable(reservations.get(id));
    }
    
    @Override
    public List<Reservation> findAll() {
        return new ArrayList<>(reservations.values());
    }
    
    @Override
    public boolean deleteById(String id) {
        return reservations.remove(id) != null;
    }
    
    @Override
    public boolean existsById(String id) {
        return reservations.containsKey(id);
    }
    
    @Override
    public long count() {
        return reservations.size();
    }
    
    /**
     * Find reservations for a specific date
     */
    public List<Reservation> findByDate(LocalDate date) {
        return reservations.values().stream()
            .filter(r -> r.getReservationTime().toLocalDate().equals(date))
            .sorted(Comparator.comparing(Reservation::getReservationTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Find reservations for a table on a specific date
     */
    public List<Reservation> findByTableAndDate(String tableId, LocalDate date) {
        return reservations.values().stream()
            .filter(r -> r.getTable().getId().equals(tableId))
            .filter(r -> r.getReservationTime().toLocalDate().equals(date))
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED ||
                        r.getStatus() == Reservation.ReservationStatus.CHECKED_IN)
            .sorted(Comparator.comparing(Reservation::getReservationTime))
            .collect(Collectors.toList());
    }
    
    /**
     * Find overlapping reservations for a table
     */
    public List<Reservation> findOverlapping(String tableId, LocalDateTime start, LocalDateTime end) {
        return reservations.values().stream()
            .filter(r -> r.getTable().getId().equals(tableId))
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
            .filter(r -> r.overlaps(start, end))
            .collect(Collectors.toList());
    }
    
    /**
     * Find reservations by customer phone
     */
    public List<Reservation> findByCustomerPhone(String phone) {
        return reservations.values().stream()
            .filter(r -> phone.equals(r.getCustomerPhone()))
            .sorted(Comparator.comparing(Reservation::getReservationTime).reversed())
            .collect(Collectors.toList());
    }
    
    /**
     * Find upcoming reservations
     */
    public List<Reservation> findUpcoming(int hours) {
        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = now.plusHours(hours);
        
        return reservations.values().stream()
            .filter(r -> r.getStatus() == Reservation.ReservationStatus.CONFIRMED)
            .filter(r -> r.getReservationTime().isAfter(now))
            .filter(r -> r.getReservationTime().isBefore(cutoff))
            .sorted(Comparator.comparing(Reservation::getReservationTime))
            .collect(Collectors.toList());
    }
}

