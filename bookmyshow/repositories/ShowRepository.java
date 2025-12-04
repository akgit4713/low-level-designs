package bookmyshow.repositories;

import bookmyshow.models.Show;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Show entity operations.
 */
public interface ShowRepository {
    void save(Show show);
    Optional<Show> findById(String id);
    List<Show> findAll();
    List<Show> findByMovieId(String movieId);
    List<Show> findByTheaterId(String theaterId);
    List<Show> findByScreenId(String screenId);
    List<Show> findByMovieIdAndCityId(String movieId, String cityId);
    List<Show> findByMovieIdAndDate(String movieId, LocalDate date);
    List<Show> findByTheaterIdAndDate(String theaterId, LocalDate date);
    void delete(String id);
    boolean exists(String id);
}



