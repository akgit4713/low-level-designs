package movierating.services;

import movierating.models.Movie;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for movie operations.
 * 
 * Interface Segregation: Only movie-related operations.
 * Dependency Inversion: High-level modules depend on this abstraction.
 */
public interface MovieService {
    
    /**
     * Add a new movie to the system.
     * @param movie The movie to add
     * @return The added movie
     */
    Movie addMovie(Movie movie);
    
    /**
     * Get a movie by its ID.
     * @param movieId The movie ID
     * @return Optional containing the movie if found
     */
    Optional<Movie> getMovieById(String movieId);
    
    /**
     * Get all movies.
     * @return List of all movies
     */
    List<Movie> getAllMovies();
    
    /**
     * Search movies by title.
     * @param title Title to search for (partial match)
     * @return List of matching movies
     */
    List<Movie> searchByTitle(String title);
    
    /**
     * Search movies by genre.
     * @param genre Genre to search for
     * @return List of matching movies
     */
    List<Movie> searchByGenre(String genre);
    
    /**
     * Delete a movie.
     * @param movieId The movie ID to delete
     * @return true if movie was deleted
     */
    boolean deleteMovie(String movieId);
}


