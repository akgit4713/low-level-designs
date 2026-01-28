package packagelocker.models;

import packagelocker.enums.CompartmentSize;

import java.util.*;

/**
 * Represents a physical locker station containing multiple compartments.
 */
public class Locker {
    
    private final String id;
    private final String location;
    private final Map<String, Compartment> compartmentsById;
    private final Map<CompartmentSize, List<Compartment>> compartmentsBySize;

    public Locker(String id, String location) {
        this.id = Objects.requireNonNull(id, "Locker ID cannot be null");
        this.location = Objects.requireNonNull(location, "Locker location cannot be null");
        this.compartmentsById = new LinkedHashMap<>();
        this.compartmentsBySize = new EnumMap<>(CompartmentSize.class);
        
        for (CompartmentSize size : CompartmentSize.values()) {
            compartmentsBySize.put(size, new ArrayList<>());
        }
    }

    public void addCompartment(Compartment compartment) {
        compartmentsById.put(compartment.getId(), compartment);
        compartmentsBySize.get(compartment.getSize()).add(compartment);
    }

    public String getId() {
        return id;
    }

    public String getLocation() {
        return location;
    }

    public Optional<Compartment> getCompartmentById(String compartmentId) {
        return Optional.ofNullable(compartmentsById.get(compartmentId));
    }

    public List<Compartment> getCompartmentsBySize(CompartmentSize size) {
        return Collections.unmodifiableList(compartmentsBySize.get(size));
    }

    public List<Compartment> getAllCompartments() {
        return Collections.unmodifiableList(new ArrayList<>(compartmentsById.values()));
    }

    public int getTotalCompartments() {
        return compartmentsById.size();
    }

    public int getAvailableCount(CompartmentSize size) {
        return (int) compartmentsBySize.get(size).stream()
                .filter(Compartment::isAvailable)
                .count();
    }

    public int getTotalAvailableCount() {
        return (int) compartmentsById.values().stream()
                .filter(Compartment::isAvailable)
                .count();
    }

    @Override
    public String toString() {
        return String.format("Locker{id=%s, location=%s, compartments=%d, available=%d}",
                id, location, getTotalCompartments(), getTotalAvailableCount());
    }
}
