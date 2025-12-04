package bookmyshow.services;

import bookmyshow.enums.Genre;
import bookmyshow.models.Movie;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for movie management.
 */
public interface MovieService {
    Movie addMovie(Movie movie);
    Optional<Movie> getMovie(String movieId);
    List<Movie> getAllMovies();
    List<Movie> searchByTitle(String title);
    List<Movie> searchByGenre(Genre genre);
    List<Movie> searchByLanguage(String language);
    void updateMovie(Movie movie);
    void deleteMovie(String movieId);
}



