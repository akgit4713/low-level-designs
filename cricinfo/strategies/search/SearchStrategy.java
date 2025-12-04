package cricinfo.strategies.search;

import java.util.List;

/**
 * Strategy interface for searching entities.
 * Allows pluggable search algorithms.
 * 
 * @param <T> The type of entity to search
 */
public interface SearchStrategy<T> {
    
    /**
     * Search for entities matching the given query.
     */
    List<T> search(List<T> entities, String query);
    
    /**
     * Get the strategy name for logging/debugging.
     */
    String getStrategyName();
}



