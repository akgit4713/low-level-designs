package concertbooking.services;

import concertbooking.models.Concert;
import concertbooking.strategies.search.SearchStrategy;

import java.util.List;

/**
 * Service interface for searching concerts
 */
public interface SearchService {
    
    /**
     * Search concerts by artist name
     */
    List<Concert> searchByArtist(String artist);
    
    /**
     * Search concerts by venue/city
     */
    List<Concert> searchByVenue(String venueOrCity);
    
    /**
     * Search concerts by date
     */
    List<Concert> searchByDate(String date);
    
    /**
     * Search using custom strategy
     */
    List<Concert> search(SearchStrategy strategy, String query);
    
    /**
     * Search with multiple criteria (AND)
     */
    List<Concert> searchWithAllCriteria(String artist, String venue, String date);
    
    /**
     * Search with multiple criteria (OR)
     */
    List<Concert> searchWithAnyCriteria(String artist, String venue, String date);
}



