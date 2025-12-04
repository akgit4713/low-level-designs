package movierating.services.impl;

import movierating.models.Movie;
import movierating.services.MovieService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of MovieService.
 * 
 * Single Responsibility: Only handles movie storage and retrieval.
 * Liskov Substitution: Can be replaced with any other MovieService implementation.
 */
public class InMemoryMovieService implements MovieService {
    
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();
    
    @Override
    public Movie addMovie(Movie movie) {
        movies.put(movie.getId(), movie);
        return movie;
    }
    
    @Override
    public Optional<Movie> getMovieById(String movieId) {
        return Optional.ofNullable(movies.get(movieId));
    }
    
    @Override
    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies.values());
    }
    
    @Override
    public List<Movie> searchByTitle(String title) {
        String searchTerm = title.toLowerCase();
        return movies.values().stream()
                .filter(m -> m.getTitle().toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Movie> searchByGenre(String genre) {
        String searchGenre = genre.toLowerCase();
        return movies.values().stream()
                .filter(m -> m.getGenre().toLowerCase().equals(searchGenre))
                .collect(Collectors.toList());
    }
    
    @Override
    public boolean deleteMovie(String movieId) {
        return movies.remove(movieId) != null;
    }
}


