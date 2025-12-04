package ridesharing.strategies.matching;

import ridesharing.enums.RideType;
import ridesharing.models.Driver;
import ridesharing.models.RideRequest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Composite strategy that delegates to different matching strategies based on ride type.
 * Follows Composite Pattern.
 */
public class CompositeMatchingStrategy implements DriverMatchingStrategy {
    
    private final Map<RideType, DriverMatchingStrategy> strategies;
    private final DriverMatchingStrategy defaultStrategy;

    public CompositeMatchingStrategy(DriverMatchingStrategy defaultStrategy) {
        this.strategies = new HashMap<>();
        this.defaultStrategy = defaultStrategy;
    }

    public void registerStrategy(RideType rideType, DriverMatchingStrategy strategy) {
        strategies.put(rideType, strategy);
    }

    @Override
    public Optional<Driver> findBestMatch(RideRequest request, List<Driver> availableDrivers) {
        return getStrategy(request.getRideType()).findBestMatch(request, availableDrivers);
    }

    @Override
    public List<Driver> findMatches(RideRequest request, List<Driver> availableDrivers, int maxResults) {
        return getStrategy(request.getRideType()).findMatches(request, availableDrivers, maxResults);
    }

    private DriverMatchingStrategy getStrategy(RideType rideType) {
        return strategies.getOrDefault(rideType, defaultStrategy);
    }

    @Override
    public String getStrategyName() {
        return "Composite Matching";
    }
}



