package ridesharing.services;

import ridesharing.models.Driver;
import ridesharing.models.RideRequest;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for matching drivers with ride requests.
 */
public interface DriverMatchingService {
    
    /**
     * Find the best matching driver for a ride request.
     */
    Optional<Driver> findBestDriver(RideRequest request);
    
    /**
     * Find multiple potential drivers for a ride request.
     */
    List<Driver> findAvailableDrivers(RideRequest request, int maxResults);
    
    /**
     * Check if drivers are available in an area.
     */
    boolean areDriversAvailable(ridesharing.models.Location location);
}



