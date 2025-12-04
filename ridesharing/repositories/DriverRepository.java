package ridesharing.repositories;

import ridesharing.enums.DriverStatus;
import ridesharing.models.Driver;
import ridesharing.models.Location;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Driver entity.
 */
public interface DriverRepository {
    
    Driver save(Driver driver);
    
    Optional<Driver> findById(String driverId);
    
    Optional<Driver> findByEmail(String email);
    
    List<Driver> findByStatus(DriverStatus status);
    
    List<Driver> findAvailableDrivers();
    
    List<Driver> findAvailableDriversNear(Location location, double radiusKm);
    
    List<Driver> findAll();
    
    void delete(String driverId);
    
    void updateLocation(String driverId, Location location);
    
    void updateStatus(String driverId, DriverStatus status);
}



