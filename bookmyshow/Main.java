package bookmyshow;

import bookmyshow.enums.Genre;
import bookmyshow.enums.PaymentMethod;
import bookmyshow.enums.SeatType;
import bookmyshow.factories.SeatFactory;
import bookmyshow.models.*;
import bookmyshow.observers.AnalyticsObserver;
import bookmyshow.observers.EmailNotificationObserver;
import bookmyshow.observers.SMSNotificationObserver;
import bookmyshow.repositories.impl.InMemoryUserRepository;

import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

/**
 * Main class demonstrating the BookMyShow system functionality.
 */
public class Main {
    
    public static void main(String[] args) {
        System.out.println("╔═══════════════════════════════════════════════════════════╗");
        System.out.println("║          BOOKMYSHOW - Movie Ticket Booking System         ║");
        System.out.println("╚═══════════════════════════════════════════════════════════╝\n");
        
        // Reset any previous instance
        BookMyShow.resetInstance();
        BookMyShow bookMyShow = BookMyShow.getInstance();
        
        // Setup observers for notifications and analytics
        AnalyticsObserver analyticsObserver = new AnalyticsObserver();
        bookMyShow.addBookingObserver(analyticsObserver);
        // Note: Email and SMS observers would need UserRepository, skipping for demo
        
        try {
            // ==================== 1. Setup Cities ====================
            System.out.println("📍 Setting up cities...");
            City bangalore = bookMyShow.addCity(new City("Bangalore", "Karnataka", "India"));
            City mumbai = bookMyShow.addCity(new City("Mumbai", "Maharashtra", "India"));
            System.out.println("Cities added: Bangalore, Mumbai\n");
            
            // ==================== 2. Add Movies ====================
            System.out.println("🎬 Adding movies...");
            
            Movie movie1 = new Movie.Builder("Inception", Duration.ofMinutes(148))
                .description("A thief who steals corporate secrets through dream-sharing technology")
                .language("English")
                .releaseDate(LocalDate.of(2024, 1, 15))
                .genre(Genre.SCIENCE_FICTION)
                .genre(Genre.ACTION)
                .genre(Genre.THRILLER)
                .director("Christopher Nolan")
                .build();
            bookMyShow.addMovie(movie1);
            
            Movie movie2 = new Movie.Builder("The Dark Knight", Duration.ofMinutes(152))
                .description("Batman faces the Joker, a criminal mastermind")
                .language("English")
                .releaseDate(LocalDate.of(2024, 2, 1))
                .genre(Genre.ACTION)
                .genre(Genre.DRAMA)
                .director("Christopher Nolan")
                .build();
            bookMyShow.addMovie(movie2);
            
            Movie movie3 = new Movie.Builder("RRR", Duration.ofMinutes(187))
                .description("A fictional story about two Indian revolutionaries")
                .language("Telugu")
                .releaseDate(LocalDate.of(2024, 1, 25))
                .genre(Genre.ACTION)
                .genre(Genre.DRAMA)
                .build();
            bookMyShow.addMovie(movie3);
            
            System.out.println("Movies added: Inception, The Dark Knight, RRR\n");
            
            // ==================== 3. Add Theaters ====================
            System.out.println("🏛️ Setting up theaters...");
            
            // Theater in Bangalore
            Theater pvr = new Theater("PVR Orion Mall", "Rajajinagar, Bangalore", bangalore.getId(), "560010");
            bookMyShow.addTheater(pvr);
            
            // Add screens to PVR
            Screen screen1 = new Screen(pvr.getId(), "IMAX Screen 1");
            bookMyShow.addScreen(pvr.getId(), screen1);
            
            // Add seats to screen using factory
            List<Seat> screen1Seats = SeatFactory.createStandardLayout(
                screen1.getId(), 4, 4, 2, 10);  // 4 regular, 4 premium, 2 recliner rows, 10 seats each
            bookMyShow.addSeatsToScreen(screen1.getId(), screen1Seats);
            
            Screen screen2 = new Screen(pvr.getId(), "Screen 2");
            bookMyShow.addScreen(pvr.getId(), screen2);
            List<Seat> screen2Seats = SeatFactory.createUniformLayout(
                screen2.getId(), 8, 12, SeatType.REGULAR);
            bookMyShow.addSeatsToScreen(screen2.getId(), screen2Seats);
            
            // Theater in Mumbai
            Theater inox = new Theater("INOX Megaplex", "Lower Parel, Mumbai", mumbai.getId(), "400013");
            bookMyShow.addTheater(inox);
            
            Screen screen3 = new Screen(inox.getId(), "VIP Screen");
            bookMyShow.addScreen(inox.getId(), screen3);
            List<Seat> screen3Seats = SeatFactory.createVIPLayout(
                screen3.getId(), 2, 4, 4, 8);  // 2 VIP, 4 premium rows, 4 wheelchair seats
            bookMyShow.addSeatsToScreen(screen3.getId(), screen3Seats);
            
            System.out.println("Theaters added: PVR Orion Mall (Bangalore), INOX Megaplex (Mumbai)\n");
            
            // ==================== 4. Create Shows ====================
            System.out.println("🎭 Scheduling shows...");
            
            // Shows for Inception at PVR
            LocalDateTime today = LocalDateTime.now().withHour(18).withMinute(0).withSecond(0).withNano(0);
            
            Show show1 = new Show(
                movie1.getId(),
                screen1.getId(),
                pvr.getId(),
                today,
                today.plus(movie1.getDuration()),
                BigDecimal.valueOf(250)  // Base price
            );
            bookMyShow.createShow(show1);
            
            Show show2 = new Show(
                movie1.getId(),
                screen1.getId(),
                pvr.getId(),
                today.plusHours(4),
                today.plusHours(4).plus(movie1.getDuration()),
                BigDecimal.valueOf(300)
            );
            bookMyShow.createShow(show2);
            
            // Show for RRR at INOX
            Show show3 = new Show(
                movie3.getId(),
                screen3.getId(),
                inox.getId(),
                today.plusDays(1),
                today.plusDays(1).plus(movie3.getDuration()),
                BigDecimal.valueOf(400)
            );
            bookMyShow.createShow(show3);
            
            System.out.println("Shows scheduled for Inception and RRR\n");
            
            // ==================== 5. Register Users ====================
            System.out.println("👤 Registering users...");
            
            User user1 = bookMyShow.registerUser(new User("John Doe", "john@email.com", "9876543210"));
            User user2 = bookMyShow.registerUser(new User("Jane Smith", "jane@email.com", "9876543211"));
            
            System.out.println("Users registered: John Doe, Jane Smith\n");
            
            // ==================== 6. Search Movies ====================
            System.out.println("🔍 Searching movies...");
            
            System.out.println("Search by title 'Inception':");
            List<Movie> searchResults = bookMyShow.searchMoviesByTitle("Inception");
            searchResults.forEach(m -> System.out.println("  - " + m.getTitle()));
            
            System.out.println("\nSearch by genre ACTION:");
            searchResults = bookMyShow.searchMoviesByGenre(Genre.ACTION);
            searchResults.forEach(m -> System.out.println("  - " + m.getTitle()));
            
            System.out.println("\nSearch by language 'Telugu':");
            searchResults = bookMyShow.searchMoviesByLanguage("Telugu");
            searchResults.forEach(m -> System.out.println("  - " + m.getTitle()));
            System.out.println();
            
            // ==================== 7. Display Available Seats ====================
            System.out.println("💺 Checking available seats for Show 1 (Inception @ PVR)...");
            bookMyShow.displayAvailableSeats(show1.getId());
            
            // ==================== 8. Book Tickets ====================
            System.out.println("🎫 Booking tickets...\n");
            
            // Get some available seats
            List<ShowSeat> availableSeats = bookMyShow.getAvailableSeats(show1.getId());
            if (availableSeats.size() >= 3) {
                List<String> seatIds = Arrays.asList(
                    availableSeats.get(0).getSeat().getId(),
                    availableSeats.get(1).getSeat().getId(),
                    availableSeats.get(2).getSeat().getId()
                );
                
                System.out.println("Step 1: Initiating booking for 3 seats...");
                Booking booking1 = bookMyShow.initiateBooking(user1.getId(), show1.getId(), seatIds);
                bookMyShow.displayBookingDetails(booking1);
                
                System.out.println("Step 2: Confirming booking with UPI payment...");
                Booking confirmedBooking = bookMyShow.confirmBooking(booking1.getId(), PaymentMethod.UPI);
                bookMyShow.displayBookingDetails(confirmedBooking);
                
                // Check available seats after booking
                System.out.println("Seats available after booking:");
                bookMyShow.displayAvailableSeats(show1.getId());
            }
            
            // ==================== 9. Another Booking ====================
            System.out.println("🎫 Another user (Jane) booking tickets...\n");
            
            availableSeats = bookMyShow.getAvailableSeats(show1.getId());
            if (availableSeats.size() >= 2) {
                List<String> seatIds = Arrays.asList(
                    availableSeats.get(0).getSeat().getId(),
                    availableSeats.get(1).getSeat().getId()
                );
                
                Booking booking2 = bookMyShow.initiateBooking(user2.getId(), show1.getId(), seatIds);
                System.out.println("Booking initiated for Jane: " + booking2.getId());
                
                Booking confirmedBooking2 = bookMyShow.confirmBooking(booking2.getId(), PaymentMethod.CREDIT_CARD);
                bookMyShow.displayBookingDetails(confirmedBooking2);
            }
            
            // ==================== 10. View User Bookings ====================
            System.out.println("📋 User's booking history...\n");
            
            List<Booking> johnBookings = bookMyShow.getUserBookings(user1.getId());
            System.out.println("John's Bookings:");
            johnBookings.forEach(b -> 
                System.out.println("  - " + b.getId().substring(0, 8) + "... | Status: " + b.getStatus() + " | Amount: ₹" + b.getTotalAmount()));
            
            List<Booking> janeBookings = bookMyShow.getUserBookings(user2.getId());
            System.out.println("\nJane's Bookings:");
            janeBookings.forEach(b -> 
                System.out.println("  - " + b.getId().substring(0, 8) + "... | Status: " + b.getStatus() + " | Amount: ₹" + b.getTotalAmount()));
            
            // ==================== 11. Analytics Summary ====================
            System.out.println();
            analyticsObserver.printSummary();
            
            // ==================== 12. Demonstrate Concurrent Booking Protection ====================
            System.out.println("🔒 Demonstrating concurrent booking protection...\n");
            
            availableSeats = bookMyShow.getAvailableSeats(show2.getId());
            if (!availableSeats.isEmpty()) {
                String contestedSeatId = availableSeats.get(0).getSeat().getId();
                
                // User 1 initiates booking
                Booking bookingA = bookMyShow.initiateBooking(
                    user1.getId(), 
                    show2.getId(), 
                    List.of(contestedSeatId)
                );
                System.out.println("John initiated booking for seat: " + contestedSeatId.substring(0, 8) + "...");
                
                // User 2 tries to book the same seat
                try {
                    bookMyShow.initiateBooking(
                        user2.getId(), 
                        show2.getId(), 
                        List.of(contestedSeatId)
                    );
                } catch (Exception e) {
                    System.out.println("✓ Seat already locked - Jane cannot book: " + e.getMessage());
                }
                
                // Confirm John's booking
                bookMyShow.confirmBooking(bookingA.getId(), PaymentMethod.WALLET);
                System.out.println("✓ John's booking confirmed successfully!\n");
            }
            
            System.out.println("╔═══════════════════════════════════════════════════════════╗");
            System.out.println("║              Demo completed successfully!                 ║");
            System.out.println("╚═══════════════════════════════════════════════════════════╝");
            
        } catch (Exception e) {
            System.err.println("Error occurred: " + e.getMessage());
            e.printStackTrace();
        }
    }
}



