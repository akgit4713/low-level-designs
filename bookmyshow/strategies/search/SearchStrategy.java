package bookmyshow.strategies.search;

import bookmyshow.models.Movie;
import bookmyshow.models.Show;
import java.util.List;

/**
 * Strategy interface for searching movies and shows.
 */
public interface SearchStrategy {
    
    /**
     * Search for movies based on criteria.
     * @param query Search query
     * @return List of matching movies
     */
    List<Movie> searchMovies(String query);
    
    /**
     * Search for shows based on criteria.
     * @param query Search query
     * @return List of matching shows
     */
    List<Show> searchShows(String query);
}



