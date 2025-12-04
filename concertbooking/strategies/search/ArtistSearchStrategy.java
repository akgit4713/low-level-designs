package concertbooking.strategies.search;

import concertbooking.models.Concert;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy to find concerts by artist name
 */
public class ArtistSearchStrategy implements SearchStrategy {
    
    @Override
    public List<Concert> search(List<Concert> concerts, String query) {
        if (query == null || query.isBlank()) {
            return concerts;
        }
        
        String lowerQuery = query.toLowerCase().trim();
        
        return concerts.stream()
            .filter(concert -> concert.getArtist().toLowerCase().contains(lowerQuery))
            .collect(Collectors.toList());
    }
    
    @Override
    public String getStrategyName() {
        return "Artist Search";
    }
}



