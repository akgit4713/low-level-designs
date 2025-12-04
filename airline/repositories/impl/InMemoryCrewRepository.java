package airline.repositories.impl;

import airline.enums.CrewRole;
import airline.models.Crew;
import airline.repositories.CrewRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of CrewRepository.
 */
public class InMemoryCrewRepository implements CrewRepository {
    
    private final ConcurrentHashMap<String, Crew> crewMembers = new ConcurrentHashMap<>();

    @Override
    public Crew save(Crew entity) {
        crewMembers.put(entity.getId(), entity);
        return entity;
    }

    @Override
    public Optional<Crew> findById(String id) {
        return Optional.ofNullable(crewMembers.get(id));
    }

    @Override
    public List<Crew> findAll() {
        return new ArrayList<>(crewMembers.values());
    }

    @Override
    public boolean deleteById(String id) {
        return crewMembers.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return crewMembers.containsKey(id);
    }

    @Override
    public long count() {
        return crewMembers.size();
    }

    @Override
    public Crew findByEmployeeId(String employeeId) {
        return crewMembers.values().stream()
                .filter(c -> c.getEmployeeId().equals(employeeId))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Crew> findByRole(CrewRole role) {
        return crewMembers.values().stream()
                .filter(c -> c.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public List<Crew> findAvailable() {
        return crewMembers.values().stream()
                .filter(Crew::isAvailable)
                .collect(Collectors.toList());
    }

    @Override
    public List<Crew> findAvailableByRole(CrewRole role) {
        return crewMembers.values().stream()
                .filter(c -> c.isAvailable() && c.getRole() == role)
                .collect(Collectors.toList());
    }

    @Override
    public List<Crew> findByCertification(String aircraftModel) {
        return crewMembers.values().stream()
                .filter(c -> c.hasCertification(aircraftModel))
                .collect(Collectors.toList());
    }
}



