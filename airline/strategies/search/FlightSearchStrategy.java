package airline.strategies.search;

import airline.models.Flight;
import airline.models.FlightSearchResult;

import java.util.List;

/**
 * Strategy interface for searching and sorting flights.
 */
public interface FlightSearchStrategy {
    
    /**
     * Sorts/filters the given flights based on the strategy's criteria.
     * 
     * @param flights The flights to sort/filter
     * @return Sorted list of flight search results
     */
    List<FlightSearchResult> sortFlights(List<FlightSearchResult> flights);
    
    /**
     * Gets the description of this search strategy.
     */
    String getDescription();
}



