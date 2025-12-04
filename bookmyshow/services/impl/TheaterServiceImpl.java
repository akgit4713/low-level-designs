package bookmyshow.services.impl;

import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.models.Screen;
import bookmyshow.models.Seat;
import bookmyshow.models.Theater;
import bookmyshow.repositories.TheaterRepository;
import bookmyshow.services.TheaterService;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of TheaterService.
 */
public class TheaterServiceImpl implements TheaterService {
    
    private final TheaterRepository theaterRepository;

    public TheaterServiceImpl(TheaterRepository theaterRepository) {
        this.theaterRepository = theaterRepository;
    }

    @Override
    public Theater addTheater(Theater theater) {
        theaterRepository.save(theater);
        return theater;
    }

    @Override
    public Optional<Theater> getTheater(String theaterId) {
        return theaterRepository.findById(theaterId);
    }

    @Override
    public List<Theater> getAllTheaters() {
        return theaterRepository.findAll();
    }

    @Override
    public List<Theater> getTheatersByCity(String cityId) {
        return theaterRepository.findByCityId(cityId);
    }

    @Override
    public Screen addScreen(String theaterId, Screen screen) {
        Theater theater = theaterRepository.findById(theaterId)
            .orElseThrow(() -> new EntityNotFoundException("Theater", theaterId));
        
        theater.addScreen(screen);
        theaterRepository.save(theater);
        return screen;
    }

    @Override
    public void addSeatsToScreen(String screenId, List<Seat> seats) {
        // Find the theater containing this screen
        Theater theater = theaterRepository.findAll().stream()
            .filter(t -> t.getScreenById(screenId) != null)
            .findFirst()
            .orElseThrow(() -> new EntityNotFoundException("Screen", screenId));
        
        Screen screen = theater.getScreenById(screenId);
        screen.addSeats(seats);
        theaterRepository.save(theater);
    }

    @Override
    public Optional<Screen> getScreen(String theaterId, String screenId) {
        return theaterRepository.findById(theaterId)
            .map(theater -> theater.getScreenById(screenId));
    }

    @Override
    public void updateTheater(Theater theater) {
        if (!theaterRepository.exists(theater.getId())) {
            throw new EntityNotFoundException("Theater", theater.getId());
        }
        theaterRepository.save(theater);
    }

    @Override
    public void deleteTheater(String theaterId) {
        if (!theaterRepository.exists(theaterId)) {
            throw new EntityNotFoundException("Theater", theaterId);
        }
        theaterRepository.delete(theaterId);
    }
}



