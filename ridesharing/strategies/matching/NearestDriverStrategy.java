package ridesharing.strategies.matching;

import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.models.RideRequest;
import ridesharing.strategies.distance.DistanceCalculationStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Matches drivers based on proximity to pickup location.
 * Simplest and most common matching strategy.
 */
public class NearestDriverStrategy implements DriverMatchingStrategy {
    
    private final DistanceCalculationStrategy distanceStrategy;
    private final double maxDistanceKm;

    public NearestDriverStrategy(DistanceCalculationStrategy distanceStrategy) {
        this(distanceStrategy, 10.0); // Default max distance: 10 km
    }

    public NearestDriverStrategy(DistanceCalculationStrategy distanceStrategy, double maxDistanceKm) {
        this.distanceStrategy = distanceStrategy;
        this.maxDistanceKm = maxDistanceKm;
    }

    @Override
    public Optional<Driver> findBestMatch(RideRequest request, List<Driver> availableDrivers) {
        return findMatches(request, availableDrivers, 1).stream().findFirst();
    }

    @Override
    public List<Driver> findMatches(RideRequest request, List<Driver> availableDrivers, int maxResults) {
        Location pickup = request.getPickupLocation();
        
        return availableDrivers.stream()
                .filter(driver -> driver.getCurrentLocation() != null)
                .filter(driver -> {
                    double distance = distanceStrategy.calculateDistance(
                            driver.getCurrentLocation(), pickup);
                    return distance <= maxDistanceKm;
                })
                .sorted(Comparator.comparingDouble(driver -> 
                        distanceStrategy.calculateDistance(driver.getCurrentLocation(), pickup)))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "Nearest Driver";
    }
}



