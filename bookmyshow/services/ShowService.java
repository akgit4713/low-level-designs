package bookmyshow.services;

import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for show management.
 */
public interface ShowService {
    Show createShow(Show show);
    Optional<Show> getShow(String showId);
    List<Show> getShowsByMovie(String movieId);
    List<Show> getShowsByTheater(String theaterId);
    List<Show> getShowsByMovieAndCity(String movieId, String cityId);
    List<Show> getShowsByMovieAndDate(String movieId, LocalDate date);
    List<Show> getShowsByTheaterAndDate(String theaterId, LocalDate date);
    List<ShowSeat> getAvailableSeats(String showId);
    void updateShow(Show show);
    void cancelShow(String showId);
}



