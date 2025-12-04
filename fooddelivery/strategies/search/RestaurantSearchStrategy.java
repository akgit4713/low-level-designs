package fooddelivery.strategies.search;

import fooddelivery.models.Location;
import fooddelivery.models.Restaurant;
import java.util.List;

/**
 * Strategy interface for restaurant search and filtering.
 * Implements Strategy Pattern for different search/sort criteria.
 */
public interface RestaurantSearchStrategy {
    
    /**
     * Filter and sort restaurants based on strategy criteria.
     * @param restaurants List of restaurants to filter
     * @param customerLocation Customer's current location
     * @return Filtered and sorted list of restaurants
     */
    List<Restaurant> search(List<Restaurant> restaurants, Location customerLocation);
    
    /**
     * Get the strategy name for display purposes.
     */
    String getStrategyName();
}



