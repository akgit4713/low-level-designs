package ridesharing.repositories.impl;

import ridesharing.models.Passenger;
import ridesharing.repositories.PassengerRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Thread-safe in-memory implementation of PassengerRepository.
 */
public class InMemoryPassengerRepository implements PassengerRepository {
    
    private final Map<String, Passenger> passengers = new ConcurrentHashMap<>();

    @Override
    public Passenger save(Passenger passenger) {
        passengers.put(passenger.getUserId(), passenger);
        return passenger;
    }

    @Override
    public Optional<Passenger> findById(String passengerId) {
        return Optional.ofNullable(passengers.get(passengerId));
    }

    @Override
    public Optional<Passenger> findByEmail(String email) {
        return passengers.values().stream()
                .filter(passenger -> email.equals(passenger.getEmail()))
                .findFirst();
    }

    @Override
    public List<Passenger> findAll() {
        return List.copyOf(passengers.values());
    }

    @Override
    public void delete(String passengerId) {
        passengers.remove(passengerId);
    }
}



