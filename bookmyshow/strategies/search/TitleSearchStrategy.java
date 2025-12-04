package bookmyshow.strategies.search;

import bookmyshow.models.Movie;
import bookmyshow.models.Show;
import bookmyshow.repositories.MovieRepository;
import bookmyshow.repositories.ShowRepository;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy that searches by movie title.
 */
public class TitleSearchStrategy implements SearchStrategy {
    
    private final MovieRepository movieRepository;
    private final ShowRepository showRepository;

    public TitleSearchStrategy(MovieRepository movieRepository, ShowRepository showRepository) {
        this.movieRepository = movieRepository;
        this.showRepository = showRepository;
    }

    @Override
    public List<Movie> searchMovies(String query) {
        return movieRepository.findByTitle(query);
    }

    @Override
    public List<Show> searchShows(String query) {
        List<Movie> movies = searchMovies(query);
        return movies.stream()
            .flatMap(movie -> showRepository.findByMovieId(movie.getId()).stream())
            .collect(Collectors.toList());
    }
}



