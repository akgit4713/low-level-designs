package ridesharing.repositories.impl;

import ridesharing.enums.RideStatus;
import ridesharing.models.Ride;
import ridesharing.repositories.RideRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Thread-safe in-memory implementation of RideRepository.
 */
public class InMemoryRideRepository implements RideRepository {
    
    private final Map<String, Ride> rides = new ConcurrentHashMap<>();

    @Override
    public Ride save(Ride ride) {
        rides.put(ride.getRideId(), ride);
        return ride;
    }

    @Override
    public Optional<Ride> findById(String rideId) {
        return Optional.ofNullable(rides.get(rideId));
    }

    @Override
    public List<Ride> findByPassengerId(String passengerId) {
        return rides.values().stream()
                .filter(ride -> ride.getPassengerId().equals(passengerId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> findByDriverId(String driverId) {
        return rides.values().stream()
                .filter(ride -> driverId.equals(ride.getDriverId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> findByStatus(RideStatus status) {
        return rides.values().stream()
                .filter(ride -> ride.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> findActiveRides() {
        return rides.values().stream()
                .filter(Ride::isActive)
                .collect(Collectors.toList());
    }

    @Override
    public List<Ride> findAll() {
        return List.copyOf(rides.values());
    }

    @Override
    public void delete(String rideId) {
        rides.remove(rideId);
    }
}



