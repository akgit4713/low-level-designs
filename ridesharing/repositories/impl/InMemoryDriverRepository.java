package ridesharing.repositories.impl;

import ridesharing.enums.DriverStatus;
import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.repositories.DriverRepository;
import ridesharing.strategies.distance.DistanceCalculationStrategy;
import ridesharing.strategies.distance.HaversineDistanceStrategy;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of DriverRepository.
 */
public class InMemoryDriverRepository implements DriverRepository {
    
    private final Map<String, Driver> drivers = new ConcurrentHashMap<>();
    private final DistanceCalculationStrategy distanceStrategy;

    public InMemoryDriverRepository() {
        this.distanceStrategy = new HaversineDistanceStrategy();
    }

    public InMemoryDriverRepository(DistanceCalculationStrategy distanceStrategy) {
        this.distanceStrategy = distanceStrategy;
    }

    @Override
    public Driver save(Driver driver) {
        drivers.put(driver.getUserId(), driver);
        return driver;
    }

    @Override
    public Optional<Driver> findById(String driverId) {
        return Optional.ofNullable(drivers.get(driverId));
    }

    @Override
    public Optional<Driver> findByEmail(String email) {
        return drivers.values().stream()
                .filter(driver -> email.equals(driver.getEmail()))
                .findFirst();
    }

    @Override
    public List<Driver> findByStatus(DriverStatus status) {
        return drivers.values().stream()
                .filter(driver -> driver.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Driver> findAvailableDrivers() {
        return findByStatus(DriverStatus.AVAILABLE);
    }

    @Override
    public List<Driver> findAvailableDriversNear(Location location, double radiusKm) {
        return drivers.values().stream()
                .filter(Driver::isAvailable)
                .filter(driver -> driver.getCurrentLocation() != null)
                .filter(driver -> {
                    double distance = distanceStrategy.calculateDistance(
                            driver.getCurrentLocation(), location);
                    return distance <= radiusKm;
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<Driver> findAll() {
        return List.copyOf(drivers.values());
    }

    @Override
    public void delete(String driverId) {
        drivers.remove(driverId);
    }

    @Override
    public void updateLocation(String driverId, Location location) {
        findById(driverId).ifPresent(driver -> driver.setCurrentLocation(location));
    }

    @Override
    public void updateStatus(String driverId, DriverStatus status) {
        findById(driverId).ifPresent(driver -> driver.setStatus(status));
    }
}



