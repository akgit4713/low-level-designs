package bookmyshow.repositories.impl;

import bookmyshow.models.Show;
import bookmyshow.models.Theater;
import bookmyshow.repositories.ShowRepository;
import bookmyshow.repositories.TheaterRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ShowRepository.
 * Thread-safe using ConcurrentHashMap.
 */
public class InMemoryShowRepository implements ShowRepository {
    private final Map<String, Show> shows = new ConcurrentHashMap<>();
    private final TheaterRepository theaterRepository;

    public InMemoryShowRepository(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @Override
    public void save(Show show) {
        shows.put(show.getId(), show);
    }

    @Override
    public Optional<Show> findById(String id) {
        return Optional.ofNullable(shows.get(id));
    }

    @Override
    public List<Show> findAll() {
        return shows.values().stream().toList();
    }

    @Override
    public List<Show> findByMovieId(String movieId) {
        return shows.values().stream()
            .filter(s -> s.getMovieId().equals(movieId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Show> findByTheaterId(String theaterId) {
        return shows.values().stream()
            .filter(s -> s.getTheaterId().equals(theaterId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Show> findByScreenId(String screenId) {
        return shows.values().stream()
            .filter(s -> s.getScreenId().equals(screenId))
            .collect(Collectors.toList());
    }

    @Override
    public List<Show> findByMovieIdAndCityId(String movieId, String cityId) {
        // Get all theater IDs in the city
        List<String> theaterIds = theaterRepository.findByCityId(cityId).stream()
            .map(Theater::getId)
            .toList();

        return shows.values().stream()
            .filter(s -> s.getMovieId().equals(movieId) && theaterIds.contains(s.getTheaterId()))
            .collect(Collectors.toList());
    }

    @Override
    public List<Show> findByMovieIdAndDate(String movieId, LocalDate date) {
        return shows.values().stream()
            .filter(s -> s.getMovieId().equals(movieId) && 
                        s.getStartTime().toLocalDate().equals(date))
            .collect(Collectors.toList());
    }

    @Override
    public List<Show> findByTheaterIdAndDate(String theaterId, LocalDate date) {
        return shows.values().stream()
            .filter(s -> s.getTheaterId().equals(theaterId) && 
                        s.getStartTime().toLocalDate().equals(date))
            .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        shows.remove(id);
    }

    @Override
    public boolean exists(String id) {
        return shows.containsKey(id);
    }
}



