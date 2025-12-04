package ridesharing.services.impl;

import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.models.RideRequest;
import ridesharing.repositories.DriverRepository;
import ridesharing.services.DriverMatchingService;
import ridesharing.strategies.matching.DriverMatchingStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of DriverMatchingService.
 * Uses configurable matching strategies.
 */
public class DriverMatchingServiceImpl implements DriverMatchingService {
    
    private final DriverRepository driverRepository;
    private final DriverMatchingStrategy matchingStrategy;
    private final double searchRadiusKm;

    public DriverMatchingServiceImpl(DriverRepository driverRepository,
                                     DriverMatchingStrategy matchingStrategy) {
        this(driverRepository, matchingStrategy, 10.0);
    }

    public DriverMatchingServiceImpl(DriverRepository driverRepository,
                                     DriverMatchingStrategy matchingStrategy,
                                     double searchRadiusKm) {
        this.driverRepository = driverRepository;
        this.matchingStrategy = matchingStrategy;
        this.searchRadiusKm = searchRadiusKm;
    }

    @Override
    public Optional<Driver> findBestDriver(RideRequest request) {
        List<Driver> availableDrivers = driverRepository.findAvailableDriversNear(
                request.getPickupLocation(), searchRadiusKm);
        
        return matchingStrategy.findBestMatch(request, availableDrivers);
    }

    @Override
    public List<Driver> findAvailableDrivers(RideRequest request, int maxResults) {
        List<Driver> availableDrivers = driverRepository.findAvailableDriversNear(
                request.getPickupLocation(), searchRadiusKm);
        
        return matchingStrategy.findMatches(request, availableDrivers, maxResults);
    }

    @Override
    public boolean areDriversAvailable(Location location) {
        return !driverRepository.findAvailableDriversNear(location, searchRadiusKm).isEmpty();
    }
}



