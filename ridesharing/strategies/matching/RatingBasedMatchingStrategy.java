package ridesharing.strategies.matching;

import ridesharing.models.Driver;
import ridesharing.models.RideRequest;
import ridesharing.enums.RideType;
import ridesharing.strategies.distance.DistanceCalculationStrategy;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Matches drivers based on a combination of rating and distance.
 * Better rated drivers get priority, but distance is still considered.
 * Ideal for premium ride types.
 */
public class RatingBasedMatchingStrategy implements DriverMatchingStrategy {
    
    private final DistanceCalculationStrategy distanceStrategy;
    private final double maxDistanceKm;
    private final double ratingWeight;
    private final double distanceWeight;

    public RatingBasedMatchingStrategy(DistanceCalculationStrategy distanceStrategy) {
        this(distanceStrategy, 15.0, 0.6, 0.4);
    }

    public RatingBasedMatchingStrategy(DistanceCalculationStrategy distanceStrategy, 
                                       double maxDistanceKm,
                                       double ratingWeight, 
                                       double distanceWeight) {
        this.distanceStrategy = distanceStrategy;
        this.maxDistanceKm = maxDistanceKm;
        this.ratingWeight = ratingWeight;
        this.distanceWeight = distanceWeight;
    }

    @Override
    public Optional<Driver> findBestMatch(RideRequest request, List<Driver> availableDrivers) {
        return findMatches(request, availableDrivers, 1).stream().findFirst();
    }

    @Override
    public List<Driver> findMatches(RideRequest request, List<Driver> availableDrivers, int maxResults) {
        // For premium rides, require higher-rated drivers
        double minRating = request.getRideType() == RideType.PREMIUM ? 4.5 : 4.0;
        
        return availableDrivers.stream()
                .filter(driver -> driver.getCurrentLocation() != null)
                .filter(driver -> driver.getRating() >= minRating)
                .filter(driver -> {
                    double distance = distanceStrategy.calculateDistance(
                            driver.getCurrentLocation(), request.getPickupLocation());
                    return distance <= maxDistanceKm;
                })
                .sorted(Comparator.comparingDouble(driver -> 
                        -calculateScore(driver, request)))
                .limit(maxResults)
                .collect(Collectors.toList());
    }

    private double calculateScore(Driver driver, RideRequest request) {
        double distance = distanceStrategy.calculateDistance(
                driver.getCurrentLocation(), request.getPickupLocation());
        
        // Normalize distance (0-1, where 1 is closest)
        double normalizedDistance = 1 - (distance / maxDistanceKm);
        
        // Normalize rating (0-1, where 1 is highest)
        double normalizedRating = (driver.getRating() - 1) / 4.0;
        
        return (normalizedRating * ratingWeight) + (normalizedDistance * distanceWeight);
    }

    @Override
    public String getStrategyName() {
        return "Rating-Based Matching";
    }
}



