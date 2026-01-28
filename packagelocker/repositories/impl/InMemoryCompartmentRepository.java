package packagelocker.repositories.impl;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;
import packagelocker.repositories.CompartmentRepository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of CompartmentRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryCompartmentRepository implements CompartmentRepository {
    
    private final Map<String, Compartment> compartmentsById = new ConcurrentHashMap<>();
    private final Map<Integer, Compartment> compartmentsByNumber = new ConcurrentHashMap<>();

    @Override
    public void save(Compartment compartment) {
        compartmentsById.put(compartment.getId(), compartment);
        compartmentsByNumber.put(compartment.getNumber(), compartment);
    }

    @Override
    public Optional<Compartment> findById(String id) {
        return Optional.ofNullable(compartmentsById.get(id));
    }

    @Override
    public Optional<Compartment> findByNumber(int number) {
        return Optional.ofNullable(compartmentsByNumber.get(number));
    }

    @Override
    public List<Compartment> findAll() {
        return new ArrayList<>(compartmentsById.values());
    }

    @Override
    public List<Compartment> findBySize(CompartmentSize size) {
        return compartmentsById.values().stream()
                .filter(c -> c.getSize() == size)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compartment> findAvailable() {
        return compartmentsById.values().stream()
                .filter(Compartment::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Compartment> findAvailableBySize(CompartmentSize size) {
        return compartmentsById.values().stream()
                .filter(Compartment::isAvailable)
                .filter(c -> c.getSize() == size)
                .collect(Collectors.toList());
    }
}
