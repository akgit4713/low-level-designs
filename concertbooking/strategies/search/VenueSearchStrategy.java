package concertbooking.strategies.search;

import concertbooking.models.Concert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy to find concerts by venue name or city
 */
public class VenueSearchStrategy implements SearchStrategy {
    
    @Override
    public List<Concert> search(List<Concert> concerts, String query) {
        if (query == null || query.isBlank()) {
            return concerts;
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        return concerts.stream()
            .filter(concert -> 
                concert.getVenue().getName().toLowerCase().contains(lowerQuery) ||
                concert.getVenue().getCity().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    @Override
    public String getStrategyName() {
        return "Venue Search";
    }
}



