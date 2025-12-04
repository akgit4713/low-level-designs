package bookmyshow.services;

import bookmyshow.models.Screen;
import bookmyshow.models.Seat;
import bookmyshow.models.Theater;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for theater management.
 */
public interface TheaterService {
    Theater addTheater(Theater theater);
    Optional<Theater> getTheater(String theaterId);
    List<Theater> getAllTheaters();
    List<Theater> getTheatersByCity(String cityId);
    Screen addScreen(String theaterId, Screen screen);
    void addSeatsToScreen(String screenId, List<Seat> seats);
    Optional<Screen> getScreen(String theaterId, String screenId);
    void updateTheater(Theater theater);
    void deleteTheater(String theaterId);
}



