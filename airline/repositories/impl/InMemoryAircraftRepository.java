package airline.repositories.impl;

import airline.enums.AircraftStatus;
import airline.models.Aircraft;
import airline.repositories.AircraftRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of AircraftRepository.
 */
public class InMemoryAircraftRepository implements AircraftRepository {
    
    private final ConcurrentHashMap<String, Aircraft> aircraft = new ConcurrentHashMap<>();

    @Override
    public Aircraft save(Aircraft entity) {
        aircraft.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Aircraft> findById(String id) {
        return Optional.ofNullable(aircraft.get(id));
    }

    @Override
    public List<Aircraft> findAll() {
        return new ArrayList<>(aircraft.values());
    }

    @Override
    public boolean deleteById(String id) {
        return aircraft.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return aircraft.containsKey(id);
    }

    @Override
    public long count() {
        return aircraft.size();
    }

    @Override
    public Aircraft findByRegistrationNumber(String registrationNumber) {
        return aircraft.values().stream()
                .filter(a -> a.getRegistrationNumber().equals(registrationNumber))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Aircraft> findByStatus(AircraftStatus status) {
        return aircraft.values().stream()
                .filter(a -> a.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Aircraft> findAvailable() {
        return findByStatus(AircraftStatus.AVAILABLE);
    }

    @Override
    public List<Aircraft> findByModel(String model) {
        return aircraft.values().stream()
                .filter(a -> a.getModel().equals(model))
                .collect(Collectors.toList());
    }
}



