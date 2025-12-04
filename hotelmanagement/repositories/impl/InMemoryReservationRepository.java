package hotelmanagement.repositories.impl;

import hotelmanagement.enums.ReservationStatus;
import hotelmanagement.models.Reservation;
import hotelmanagement.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of ReservationRepository
 */
public class InMemoryReservationRepository implements ReservationRepository {
    
    private final ConcurrentHashMap<String, Reservation> reservations = new ConcurrentHashMap<>();
    
    @Override
    public Reservation save(Reservation reservation) {
        reservations.put(reservation.getId(), reservation);
        return reservation;
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
    
    @Override
    public List<Reservation> findByGuestId(String guestId) {
        return reservations.values().stream()
            .filter(r -> r.getGuest().getId().equals(guestId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findByRoomId(String roomId) {
        return reservations.values().stream()
            .filter(r -> r.getRoom().getId().equals(roomId))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.values().stream()
            .filter(r -> r.getStatus() == status)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findByCheckInDate(LocalDate date) {
        return reservations.values().stream()
            .filter(r -> r.getCheckInDate().equals(date))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findByCheckOutDate(LocalDate date) {
        return reservations.values().stream()
            .filter(r -> r.getCheckOutDate().equals(date))
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findActiveReservations() {
        return reservations.values().stream()
            .filter(r -> r.getStatus() == ReservationStatus.CHECKED_IN)
            .collect(Collectors.toList());
    }
    
    @Override
    public List<Reservation> findOverlappingReservations(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return reservations.values().stream()
            .filter(r -> r.getRoom().getId().equals(roomId))
            .filter(r -> !r.getStatus().isTerminal()) // Only non-cancelled/completed
            .filter(r -> datesOverlap(r.getCheckInDate(), r.getCheckOutDate(), checkIn, checkOut))
            .collect(Collectors.toList());
    }
    
    @Override
    public boolean isRoomAvailable(String roomId, LocalDate checkIn, LocalDate checkOut) {
        return findOverlappingReservations(roomId, checkIn, checkOut).isEmpty();
    }
    
    @Override
    public List<Reservation> findUpcomingReservations() {
        LocalDate today = LocalDate.now();
        return reservations.values().stream()
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED)
            .filter(r -> !r.getCheckInDate().isBefore(today))
            .collect(Collectors.toList());
    }
    
    /**
     * Check if two date ranges overlap
     */
    private boolean datesOverlap(LocalDate start1, LocalDate end1, LocalDate start2, LocalDate end2) {
        return !end1.isBefore(start2) && !end2.isBefore(start1) &&
               !start1.equals(end2) && !start2.equals(end1);
    }
}



