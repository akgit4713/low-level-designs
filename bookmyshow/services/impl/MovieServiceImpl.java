package bookmyshow.services.impl;

import bookmyshow.enums.Genre;
import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.models.Movie;
import bookmyshow.repositories.MovieRepository;
import bookmyshow.services.MovieService;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of MovieService.
 */
public class MovieServiceImpl implements MovieService {
    
    private final MovieRepository movieRepository;

    public MovieServiceImpl(MovieRepository movieRepository) {
        this.movieRepository = movieRepository;
    }

    @Override
    public Movie addMovie(Movie movie) {
        movieRepository.save(movie);
        return movie;
    }

    @Override
    public Optional<Movie> getMovie(String movieId) {
        return movieRepository.findById(movieId);
    }

    @Override
    public List<Movie> getAllMovies() {
        return movieRepository.findAll();
    }

    @Override
    public List<Movie> searchByTitle(String title) {
        return movieRepository.findByTitle(title);
    }

    @Override
    public List<Movie> searchByGenre(Genre genre) {
        return movieRepository.findByGenre(genre);
    }

    @Override
    public List<Movie> searchByLanguage(String language) {
        return movieRepository.findByLanguage(language);
    }

    @Override
    public void updateMovie(Movie movie) {
        if (!movieRepository.exists(movie.getId())) {
            throw new EntityNotFoundException("Movie", movie.getId());
        }
        movieRepository.save(movie);
    }

    @Override
    public void deleteMovie(String movieId) {
        if (!movieRepository.exists(movieId)) {
            throw new EntityNotFoundException("Movie", movieId);
        }
        movieRepository.delete(movieId);
    }
}



