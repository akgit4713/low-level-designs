package bookmyshow.strategies.search;

import bookmyshow.enums.Genre;
import bookmyshow.models.Movie;
import bookmyshow.models.Show;
import bookmyshow.repositories.MovieRepository;
import bookmyshow.repositories.ShowRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that searches by movie genre.
 */
public class GenreSearchStrategy implements SearchStrategy {
    
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public GenreSearchStrategy(MovieRepository movieRepository, ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    @Override
    public List<Movie> searchMovies(String query) {
        try {
            Genre genre = Genre.valueOf(query.toUpperCase());
            return movieRepository.findByGenre(genre);
        } catch (IllegalArgumentException e) {
            return List.of();
        }
    }

    @Override
    public List<Show> searchShows(String query) {
        List<Movie> movies = searchMovies(query);
        return movies.stream()
            .flatMap(movie -> showRepository.findByMovieId(movie.getId()).stream())
            .collect(Collectors.toList());
    }
}



