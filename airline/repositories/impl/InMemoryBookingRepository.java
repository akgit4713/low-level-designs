package airline.repositories.impl;

import airline.enums.BookingStatus;
import airline.models.Booking;
import airline.repositories.BookingRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of BookingRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryBookingRepository implements BookingRepository {
    
    private final ConcurrentHashMap<String, Booking> bookings = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, String> pnrToId = new ConcurrentHashMap<>();

    @Override
    public Booking save(Booking booking) {
        bookings.put(booking.getId(), booking);
        pnrToId.put(booking.getPnr(), booking.getId());
        return booking;
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
        Booking removed = bookings.remove(id);
        if (removed != null) {
            pnrToId.remove(removed.getPnr());
            return true;
        }
        return false;
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
    public Optional<Booking> findByPnr(String pnr) {
        String id = pnrToId.get(pnr);
        if (id == null) return Optional.empty();
        return Optional.ofNullable(bookings.get(id));
    }

    @Override
    public List<Booking> findByFlightNumber(String flightNumber) {
        return bookings.values().stream()
                .filter(b -> b.getFlight().getFlightNumber().equals(flightNumber))
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByStatus(BookingStatus status) {
        return bookings.values().stream()
                .filter(b -> b.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Booking> findByPassengerEmail(String email) {
        return bookings.values().stream()
                .filter(b -> b.getPassengers().stream()
                        .anyMatch(bp -> bp.getPassenger().getEmail().equals(email)))
                .collect(Collectors.toList());
    }
}



