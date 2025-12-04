package bookmyshow.repositories;

import bookmyshow.enums.Genre;
import bookmyshow.models.Movie;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Movie entity operations.
 */
public interface MovieRepository {
    void save(Movie movie);
    Optional<Movie> findById(String id);
    List<Movie> findAll();
    List<Movie> findByTitle(String title);
    List<Movie> findByGenre(Genre genre);
    List<Movie> findByLanguage(String language);
    void delete(String id);
    boolean exists(String id);
}



