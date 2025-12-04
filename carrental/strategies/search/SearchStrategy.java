package carrental.strategies.search;

import carrental.models.Car;
import carrental.models.SearchCriteria;

import java.util.List;

/**
 * Strategy interface for searching cars.
 * Allows different search algorithms and criteria processing.
 */
public interface SearchStrategy {
    
    /**
     * Filters a list of cars based on search criteria.
     * 
     * @param cars List of cars to filter
     * @param criteria Search criteria to apply
     * @return Filtered list of cars matching criteria
     */
    List<Car> search(List<Car> cars, SearchCriteria criteria);
    
    /**
     * Returns the name of this search strategy.
     */
    String getStrategyName();
}



