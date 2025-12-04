package concertbooking.strategies.search;

import concertbooking.models.Concert;

import java.util.List;

/**
 * Strategy interface for searching concerts
 */
public interface SearchStrategy {
    
    /**
     * Search concerts based on the strategy's criteria
     * @param concerts List of all concerts to search
     * @param query Search query/criteria
     * @return Filtered list of concerts matching the criteria
     */
    List<Concert> search(List<Concert> concerts, String query);
    
    /**
     * Get the name of this search strategy
     */
    String getStrategyName();
}



