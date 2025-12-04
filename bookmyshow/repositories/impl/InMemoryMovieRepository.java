package bookmyshow.repositories.impl;

import bookmyshow.enums.Genre;
import bookmyshow.models.Movie;
import bookmyshow.repositories.MovieRepository;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of MovieRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryMovieRepository implements MovieRepository {
    private final Map<String, Movie> movies = new ConcurrentHashMap<>();

    @Override
    public void save(Movie movie) {
        movies.put(movie.getId(), movie);
    }

    @Override
    public Optional<Movie> findById(String id) {
        return Optional.ofNullable(movies.get(id));
    }

    @Override
    public List<Movie> findAll() {
        return movies.values().stream().toList();
    }

    @Override
    public List<Movie> findByTitle(String title) {
        return movies.values().stream()
            .filter(m -> m.getTitle().toLowerCase().contains(title.toLowerCase()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByGenre(Genre genre) {
        return movies.values().stream()
            .filter(m -> m.getGenres().contains(genre))
            .collect(Collectors.toList());
    }

    @Override
    public List<Movie> findByLanguage(String language) {
        return movies.values().stream()
            .filter(m -> m.getLanguage().equalsIgnoreCase(language))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        movies.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return movies.containsKey(id);
    }
}



