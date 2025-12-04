package ridesharing.repositories;

import ridesharing.enums.RideStatus;
import ridesharing.models.Ride;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Ride entity.
 * Follows Repository Pattern (DIP - abstracts data access).
 */
public interface RideRepository {
    
    Ride save(Ride ride);
    
    Optional<Ride> findById(String rideId);
    
    List<Ride> findByPassengerId(String passengerId);
    
    List<Ride> findByDriverId(String driverId);
    
    List<Ride> findByStatus(RideStatus status);
    
    List<Ride> findActiveRides();
    
    List<Ride> findAll();
    
    void delete(String rideId);
}



