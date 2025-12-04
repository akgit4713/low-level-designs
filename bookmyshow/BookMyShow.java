package bookmyshow;

import bookmyshow.enums.Genre;
import bookmyshow.enums.PaymentMethod;
import bookmyshow.models.*;
import bookmyshow.observers.BookingObserver;
import bookmyshow.repositories.*;
import bookmyshow.repositories.impl.*;
import bookmyshow.services.*;
import bookmyshow.services.impl.*;
import bookmyshow.strategies.pricing.PricingStrategy;
import bookmyshow.strategies.pricing.WeekendPricingStrategy;
import bookmyshow.strategies.refund.RefundStrategy;
import bookmyshow.strategies.refund.TimeBasedRefundStrategy;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * Main facade for the BookMyShow system.
 * Provides a unified interface to all subsystems.
 * Implements Singleton pattern for thread-safe access.
 */
public class BookMyShow {
    
    private static volatile BookMyShow instance;
    
    // Services
    private final MovieService movieService;
    private final TheaterService theaterService;
    private final ShowService showService;
    private final BookingServiceImpl bookingService;
    private final PaymentService paymentService;
    private final UserService userService;
    private final CityService cityService;
    
    private BookMyShow() {
        // Initialize repositories
        CityRepository cityRepository = new InMemoryCityRepository();
        MovieRepository movieRepository = new InMemoryMovieRepository();
        TheaterRepository theaterRepository = new InMemoryTheaterRepository();
        ShowRepository showRepository = new InMemoryShowRepository(theaterRepository);
        BookingRepository bookingRepository = new InMemoryBookingRepository();
        UserRepository userRepository = new InMemoryUserRepository();
        PaymentRepository paymentRepository = new InMemoryPaymentRepository();
        
        // Initialize strategies
        PricingStrategy pricingStrategy = new WeekendPricingStrategy();
        RefundStrategy refundStrategy = new TimeBasedRefundStrategy();
        
        // Initialize services
        this.cityService = new CityServiceImpl(cityRepository);
        this.movieService = new MovieServiceImpl(movieRepository);
        this.theaterService = new TheaterServiceImpl(theaterRepository);
        this.showService = new ShowServiceImpl(showRepository, theaterRepository, pricingStrategy);
        this.paymentService = new PaymentServiceImpl(paymentRepository);
        this.userService = new UserServiceImpl(userRepository);
        this.bookingService = new BookingServiceImpl(
            bookingRepository,
            showRepository,
            userRepository,
            paymentService,
            pricingStrategy,
            refundStrategy
        );
    }
    
    /**
     * Get the singleton instance of BookMyShow.
     * Thread-safe using double-checked locking.
     */
    public static BookMyShow getInstance() {
        if (instance == null) {
            synchronized (BookMyShow.class) {
                if (instance == null) {
                    instance = new BookMyShow();
                }
            }
        }
        return instance;
    }
    
    /**
     * Reset instance (useful for testing).
     */
    public static void resetInstance() {
        synchronized (BookMyShow.class) {
            instance = null;
        }
    }
    
    // ==================== City Operations ====================
    
    public City addCity(City city) {
        return cityService.addCity(city);
    }
    
    public List<City> getAllCities() {
        return cityService.getAllCities();
    }
    
    public Optional<City> getCityByName(String name) {
        return cityService.getCityByName(name);
    }
    
    // ==================== Movie Operations ====================
    
    public Movie addMovie(Movie movie) {
        return movieService.addMovie(movie);
    }
    
    public Optional<Movie> getMovie(String movieId) {
        return movieService.getMovie(movieId);
    }
    
    public List<Movie> getAllMovies() {
        return movieService.getAllMovies();
    }
    
    public List<Movie> searchMoviesByTitle(String title) {
        return movieService.searchByTitle(title);
    }
    
    public List<Movie> searchMoviesByGenre(Genre genre) {
        return movieService.searchByGenre(genre);
    }
    
    public List<Movie> searchMoviesByLanguage(String language) {
        return movieService.searchByLanguage(language);
    }
    
    // ==================== Theater Operations ====================
    
    public Theater addTheater(Theater theater) {
        return theaterService.addTheater(theater);
    }
    
    public Optional<Theater> getTheater(String theaterId) {
        return theaterService.getTheater(theaterId);
    }
    
    public List<Theater> getTheatersByCity(String cityId) {
        return theaterService.getTheatersByCity(cityId);
    }
    
    public Screen addScreen(String theaterId, Screen screen) {
        return theaterService.addScreen(theaterId, screen);
    }
    
    public void addSeatsToScreen(String screenId, List<Seat> seats) {
        theaterService.addSeatsToScreen(screenId, seats);
    }
    
    // ==================== Show Operations ====================
    
    public Show createShow(Show show) {
        return showService.createShow(show);
    }
    
    public Optional<Show> getShow(String showId) {
        return showService.getShow(showId);
    }
    
    public List<Show> getShowsByMovie(String movieId) {
        return showService.getShowsByMovie(movieId);
    }
    
    public List<Show> getShowsByMovieAndCity(String movieId, String cityId) {
        return showService.getShowsByMovieAndCity(movieId, cityId);
    }
    
    public List<Show> getShowsByMovieAndDate(String movieId, LocalDate date) {
        return showService.getShowsByMovieAndDate(movieId, date);
    }
    
    public List<ShowSeat> getAvailableSeats(String showId) {
        return showService.getAvailableSeats(showId);
    }
    
    // ==================== User Operations ====================
    
    public User registerUser(User user) {
        return userService.registerUser(user);
    }
    
    public Optional<User> getUser(String userId) {
        return userService.getUser(userId);
    }
    
    // ==================== Booking Operations ====================
    
    public Booking initiateBooking(String userId, String showId, List<String> seatIds) {
        return bookingService.initiateBooking(userId, showId, seatIds);
    }
    
    public Booking confirmBooking(String bookingId, PaymentMethod paymentMethod) {
        return bookingService.confirmBooking(bookingId, paymentMethod);
    }
    
    public Booking cancelBooking(String bookingId) {
        return bookingService.cancelBooking(bookingId);
    }
    
    public Optional<Booking> getBooking(String bookingId) {
        return bookingService.getBooking(bookingId);
    }
    
    public List<Booking> getUserBookings(String userId) {
        return bookingService.getUserBookings(userId);
    }
    
    public void processExpiredBookings() {
        bookingService.processExpiredBookings();
    }
    
    // ==================== Observer Management ====================
    
    public void addBookingObserver(BookingObserver observer) {
        bookingService.addObserver(observer);
    }
    
    public void removeBookingObserver(BookingObserver observer) {
        bookingService.removeObserver(observer);
    }
    
    // ==================== Display Methods ====================
    
    public void displayAvailableSeats(String showId) {
        List<ShowSeat> availableSeats = getAvailableSeats(showId);
        
        System.out.println("\n=== Available Seats ===");
        if (availableSeats.isEmpty()) {
            System.out.println("No seats available!");
            return;
        }
        
        String currentRow = "";
        StringBuilder rowSeats = new StringBuilder();
        
        for (ShowSeat showSeat : availableSeats) {
            Seat seat = showSeat.getSeat();
            if (!seat.getRowLabel().equals(currentRow)) {
                if (!currentRow.isEmpty()) {
                    System.out.println("Row " + currentRow + ": " + rowSeats);
                    rowSeats = new StringBuilder();
                }
                currentRow = seat.getRowLabel();
            }
            rowSeats.append(seat.getSeatLabel())
                    .append(" (₹").append(showSeat.getPrice()).append(") ");
        }
        if (!currentRow.isEmpty()) {
            System.out.println("Row " + currentRow + ": " + rowSeats);
        }
        System.out.println("=======================\n");
    }
    
    public void displayBookingDetails(Booking booking) {
        System.out.println("\n=== Booking Details ===");
        System.out.println("Booking ID: " + booking.getId());
        System.out.println("Status: " + booking.getStatus());
        System.out.println("Number of Seats: " + booking.getNumberOfSeats());
        System.out.println("Total Amount: ₹" + booking.getTotalAmount());
        System.out.println("Created At: " + booking.getCreatedAt());
        if (booking.getConfirmedAt() != null) {
            System.out.println("Confirmed At: " + booking.getConfirmedAt());
        }
        System.out.println("=======================\n");
    }
}



