package carrental.repositories.impl;

import carrental.enums.ReservationStatus;
import carrental.models.Reservation;
import carrental.repositories.ReservationRepository;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of ReservationRepository.
 */
public class InMemoryReservationRepository implements ReservationRepository {
    
    private final Map<String, Reservation> reservations = new ConcurrentHashMap<>();

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
    public List<Reservation> findByCustomerId(String customerId) {
        return reservations.values().stream()
            .filter(r -> r.getCustomer().getId().equals(customerId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCarId(String carId) {
        return reservations.values().stream()
            .filter(r -> r.getCar().getId().equals(carId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStatus(ReservationStatus status) {
        return reservations.values().stream()
            .filter(r -> r.getStatus() == status)
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByCarIdAndDateRange(String carId, LocalDate startDate, LocalDate endDate) {
        return reservations.values().stream()
            .filter(r -> r.getCar().getId().equals(carId))
            .filter(r -> r.overlaps(startDate, endDate))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findActiveByCarIdAndDateRange(String carId, LocalDate startDate, LocalDate endDate) {
        return reservations.values().stream()
            .filter(r -> r.getCar().getId().equals(carId))
            .filter(r -> r.getStatus() == ReservationStatus.CONFIRMED || 
                        r.getStatus() == ReservationStatus.ACTIVE ||
                        r.getStatus() == ReservationStatus.PENDING)
            .filter(r -> r.overlaps(startDate, endDate))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByStartDate(LocalDate startDate) {
        return reservations.values().stream()
            .filter(r -> r.getStartDate().equals(startDate))
            .collect(Collectors.toList());
    }

    @Override
    public List<Reservation> findByEndDate(LocalDate endDate) {
        return reservations.values().stream()
            .filter(r -> r.getEndDate().equals(endDate))
            .collect(Collectors.toList());
    }
}



