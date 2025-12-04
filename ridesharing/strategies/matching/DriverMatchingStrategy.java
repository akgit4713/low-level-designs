package ridesharing.strategies.matching;

import ridesharing.models.Driver;
import ridesharing.models.Location;
import ridesharing.models.RideRequest;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for matching drivers with ride requests.
 * Follows Strategy Pattern (OCP - can add new matching algorithms).
 */
public interface DriverMatchingStrategy {
    
    /**
     * Find the best matching driver for a ride request.
     *
     * @param request The ride request
     * @param availableDrivers List of available drivers
     * @return Optional containing the matched driver, or empty if no match found
     */
    Optional<Driver> findBestMatch(RideRequest request, List<Driver> availableDrivers);
    
    /**
     * Find multiple potential matches for a ride request, ranked by preference.
     *
     * @param request The ride request
     * @param availableDrivers List of available drivers
     * @param maxResults Maximum number of results to return
     * @return List of matched drivers, ordered by preference
     */
    List<Driver> findMatches(RideRequest request, List<Driver> availableDrivers, int maxResults);
    
    /**
     * Get the name of this matching strategy.
     */
    String getStrategyName();
}



