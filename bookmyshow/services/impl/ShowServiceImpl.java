package bookmyshow.services.impl;

import bookmyshow.enums.ShowStatus;
import bookmyshow.exceptions.EntityNotFoundException;
import bookmyshow.exceptions.InvalidOperationException;
import bookmyshow.models.Screen;
import bookmyshow.models.Seat;
import bookmyshow.models.Show;
import bookmyshow.models.ShowSeat;
import bookmyshow.models.Theater;
import bookmyshow.repositories.ShowRepository;
import bookmyshow.repositories.TheaterRepository;
import bookmyshow.services.ShowService;
import bookmyshow.strategies.pricing.PricingStrategy;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of ShowService.
 */
public class ShowServiceImpl implements ShowService {
    
    private final ShowRepository showRepository;
    private final TheaterRepository theaterRepository;
    private final PricingStrategy pricingStrategy;

    public ShowServiceImpl(ShowRepository showRepository, 
                          TheaterRepository theaterRepository,
                          PricingStrategy pricingStrategy) {
        this.showRepository = showRepository;
        this.theaterRepository = theaterRepository;
        this.pricingStrategy = pricingStrategy;
    }

    @Override
    public Show createShow(Show show) {
        // Get theater and screen to initialize ShowSeats
        Theater theater = theaterRepository.findById(show.getTheaterId())
            .orElseThrow(() -> new EntityNotFoundException("Theater", show.getTheaterId()));
        
        Screen screen = theater.getScreenById(show.getScreenId());
        if (screen == null) {
            throw new EntityNotFoundException("Screen", show.getScreenId());
        }
        
        // Create ShowSeat for each seat in the screen
        for (Seat seat : screen.getSeats()) {
            BigDecimal seatPrice = show.getBasePrice().multiply(
                BigDecimal.valueOf(seat.getSeatType().getPriceMultiplier())
            );
            ShowSeat showSeat = new ShowSeat(show.getId(), seat, seatPrice);
            show.addShowSeat(showSeat);
        }
        
        showRepository.save(show);
        return show;
    }

    @Override
    public Optional<Show> getShow(String showId) {
        return showRepository.findById(showId);
    }

    @Override
    public List<Show> getShowsByMovie(String movieId) {
        return showRepository.findByMovieId(movieId);
    }

    @Override
    public List<Show> getShowsByTheater(String theaterId) {
        return showRepository.findByTheaterId(theaterId);
    }

    @Override
    public List<Show> getShowsByMovieAndCity(String movieId, String cityId) {
        return showRepository.findByMovieIdAndCityId(movieId, cityId);
    }

    @Override
    public List<Show> getShowsByMovieAndDate(String movieId, LocalDate date) {
        return showRepository.findByMovieIdAndDate(movieId, date);
    }

    @Override
    public List<Show> getShowsByTheaterAndDate(String theaterId, LocalDate date) {
        return showRepository.findByTheaterIdAndDate(theaterId, date);
    }

    @Override
    public List<ShowSeat> getAvailableSeats(String showId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new EntityNotFoundException("Show", showId));
        
        return show.getAvailableSeats();
    }

    @Override
    public void updateShow(Show show) {
        if (!showRepository.exists(show.getId())) {
            throw new EntityNotFoundException("Show", show.getId());
        }
        showRepository.save(show);
    }

    @Override
    public void cancelShow(String showId) {
        Show show = showRepository.findById(showId)
            .orElseThrow(() -> new EntityNotFoundException("Show", showId));
        
        if (show.getStatus() == ShowStatus.COMPLETED) {
            throw new InvalidOperationException("Cannot cancel a completed show");
        }
        
        show.setStatus(ShowStatus.CANCELLED);
        showRepository.save(show);
    }
}



