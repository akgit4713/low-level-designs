package musicstreaming.strategies.search;

import java.util.List;

/**
 * Strategy interface for different search algorithms.
 * Implements the Strategy pattern for extensible search functionality.
 * 
 * @param <T> The type of result returned by the search
 */
public interface SearchStrategy<T> {
    
    /**
     * Execute the search with the given query.
     * 
     * @param query The search query string
     * @param limit Maximum number of results to return
     * @return List of matching results
     */
    List<T> search(String query, int limit);
    
    /**
     * Get the type of search this strategy performs.
     */
    String getSearchType();
}



