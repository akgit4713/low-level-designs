package concertbooking.services.impl;

import concertbooking.models.Concert;
import concertbooking.repositories.ConcertRepository;
import concertbooking.services.SearchService;
import concertbooking.strategies.search.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of SearchService using Strategy pattern
 */
public class SearchServiceImpl implements SearchService {
    
    private final ConcertRepository concertRepository;
    private final SearchStrategy artistSearchStrategy;
    private final SearchStrategy venueSearchStrategy;
    private final SearchStrategy dateSearchStrategy;
    
    public SearchServiceImpl(ConcertRepository concertRepository) {
        this.concertRepository = concertRepository;
        this.artistSearchStrategy = new ArtistSearchStrategy();
        this.venueSearchStrategy = new VenueSearchStrategy();
        this.dateSearchStrategy = new DateSearchStrategy();
    }
    
    @Override
    public List<Concert> searchByArtist(String artist) {
        return search(artistSearchStrategy, artist);
    }
    
    @Override
    public List<Concert> searchByVenue(String venueOrCity) {
        return search(venueSearchStrategy, venueOrCity);
    }
    
    @Override
    public List<Concert> searchByDate(String date) {
        return search(dateSearchStrategy, date);
    }
    
    @Override
    public List<Concert> search(SearchStrategy strategy, String query) {
        List<Concert> allConcerts = concertRepository.findUpcoming();
        return strategy.search(allConcerts, query);
    }
    
    @Override
    public List<Concert> searchWithAllCriteria(String artist, String venue, String date) {
        List<Concert> results = concertRepository.findUpcoming();
        
        if (artist != null && !artist.isBlank()) {
            results = artistSearchStrategy.search(results, artist);
        }
        
        if (venue != null && !venue.isBlank()) {
            results = venueSearchStrategy.search(results, venue);
        }
        
        if (date != null && !date.isBlank()) {
            results = dateSearchStrategy.search(results, date);
        }
        
        return results;
    }
    
    @Override
    public List<Concert> searchWithAnyCriteria(String artist, String venue, String date) {
        List<Concert> allConcerts = concertRepository.findUpcoming();
        List<Concert> results = new ArrayList<>();
        
        if (artist != null && !artist.isBlank()) {
            results.addAll(artistSearchStrategy.search(allConcerts, artist));
        }
        
        if (venue != null && !venue.isBlank()) {
            results.addAll(venueSearchStrategy.search(allConcerts, venue));
        }
        
        if (date != null && !date.isBlank()) {
            results.addAll(dateSearchStrategy.search(allConcerts, date));
        }
        
        // Remove duplicates while preserving order
        return results.stream()
            .distinct()
            .collect(Collectors.toList());
    }
}



