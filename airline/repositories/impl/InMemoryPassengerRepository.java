package airline.repositories.impl;

import airline.models.Passenger;
import airline.repositories.PassengerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of PassengerRepository.
 */
public class InMemoryPassengerRepository implements PassengerRepository {
    
    private final ConcurrentHashMap<String, Passenger> passengers = new ConcurrentHashMap<>();

    @Override
    public Passenger save(Passenger entity) {
        passengers.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Passenger> findById(String id) {
        return Optional.ofNullable(passengers.get(id));
    }

    @Override
    public List<Passenger> findAll() {
        return new ArrayList<>(passengers.values());
    }

    @Override
    public boolean deleteById(String id) {
        return passengers.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return passengers.containsKey(id);
    }

    @Override
    public long count() {
        return passengers.size();
    }

    @Override
    public Optional<Passenger> findByEmail(String email) {
        return passengers.values().stream()
                .filter(p -> p.getEmail().equals(email))
                .findFirst();
    }

    @Override
    public Optional<Passenger> findByPassportNumber(String passportNumber) {
        return passengers.values().stream()
                .filter(p -> p.getPassportNumber() != null && 
                             p.getPassportNumber().equals(passportNumber))
                .findFirst();
    }
}



