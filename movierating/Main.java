package movierating;

import movierating.factories.MovieFactory;
import movierating.factories.RatingSystemFactory;
import movierating.factories.RatingSystemFactory.RatingSystem;
import movierating.factories.UserFactory;
import movierating.models.*;
import movierating.services.MovieService;
import movierating.services.RatingService;
import movierating.services.UserService;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Movie Rating System.
 * Demonstrates the functionality of the rating system.
 */
public class Main {
    
    private static RatingSystem ratingSystem;
    private static MovieService movieService;
    private static UserService userService;
    private static RatingService ratingService;
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║          🎬 MOVIE RATING SYSTEM - DEMO 🎬                      ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝\n");
        
        // Initialize the rating system
        ratingSystem = RatingSystemFactory.createDefaultRatingSystem();
        movieService = ratingSystem.getMovieService();
        userService = ratingSystem.getUserService();
        ratingService = ratingSystem.getRatingService();
        
        // Run the demo
        runDemo();
        
        // Interactive mode
        runInteractiveMode();
    }
    
    private static void runDemo() {
        System.out.println("=== Setting up demo data ===\n");
        
        // Create sample movies
        Movie movie1 = movieService.addMovie(
            MovieFactory.createMovie("The Shawshank Redemption", "Frank Darabont", 1994, "Drama",
                "Two imprisoned men bond over a number of years, finding solace and eventual redemption through acts of common decency.")
        );
        
        Movie movie2 = movieService.addMovie(
            MovieFactory.createMovie("Inception", "Christopher Nolan", 2010, "Sci-Fi",
                "A thief who steals corporate secrets through dream-sharing technology is given the inverse task of planting an idea.")
        );
        
        Movie movie3 = movieService.addMovie(
            MovieFactory.createMovie("The Dark Knight", "Christopher Nolan", 2008, "Action",
                "When the menace known as the Joker wreaks havoc and chaos on the people of Gotham, Batman must accept one of the greatest psychological tests.")
        );
        
        System.out.println("📽️  Added movies:");
        movieService.getAllMovies().forEach(m -> System.out.println("   - " + m.getTitle() + " (" + m.getReleaseYear() + ")"));
        System.out.println();
        
        // Create sample users with different levels
        User noviceUser = userService.registerUser(UserFactory.createUser("newbie_viewer", "newbie@email.com"));
        User proUser = userService.registerUser(UserFactory.createUserWithLevel("movie_expert", "expert@email.com", UserLevel.PRO));
        User masterUser = userService.registerUser(UserFactory.createUserWithLevel("cinema_master", "master@email.com", UserLevel.MASTER));
        
        System.out.println("👥 Created users:");
        userService.getAllUsers().forEach(u -> System.out.println("   - " + u.getUsername() + " [" + u.getLevel().getDisplayName() + "]"));
        System.out.println();
        
        // Add ratings from different users
        System.out.println("=== Adding ratings ===\n");
        
        // Novice user rates movies
        Rating r1 = ratingService.createRating(noviceUser.getId(), movie1.getId(), RatingValue.FIVE_STARS, 
            "Amazing movie! One of the best I've ever seen.");
        System.out.println("📝 " + noviceUser.getUsername() + " rated '" + movie1.getTitle() + "': " + r1.getRatingValue());
        
        // Pro user rates movies
        Rating r2 = ratingService.createRating(proUser.getId(), movie1.getId(), RatingValue.FOUR_STARS, 
            "Great story and acting. A classic.");
        System.out.println("📝 " + proUser.getUsername() + " rated '" + movie1.getTitle() + "': " + r2.getRatingValue());
        
        // Master user rates movies
        Rating r3 = ratingService.createRating(masterUser.getId(), movie1.getId(), RatingValue.FIVE_STARS, 
            "A masterpiece of cinema. Exceptional in every aspect.");
        System.out.println("📝 " + masterUser.getUsername() + " rated '" + movie1.getTitle() + "': " + r3.getRatingValue());
        
        // More ratings for movie2
        ratingService.createRating(proUser.getId(), movie2.getId(), RatingValue.FIVE_STARS, "Mind-bending!");
        ratingService.createRating(masterUser.getId(), movie2.getId(), RatingValue.FOUR_STARS, "Very innovative.");
        
        // Ratings for movie3
        ratingService.createRating(noviceUser.getId(), movie3.getId(), RatingValue.FIVE_STARS, "Best superhero movie ever!");
        ratingService.createRating(masterUser.getId(), movie3.getId(), RatingValue.FIVE_STARS, "Heath Ledger's performance is legendary.");
        
        System.out.println();
        
        // Show aggregated ratings (weighted by user level)
        System.out.println("=== Aggregated Ratings (Weighted by User Level) ===\n");
        showAggregatedRatings();
        
        // Demonstrate voting and level changes
        System.out.println("=== Demonstrating Helpful Votes ===\n");
        
        // Pro and master users vote on novice's reviews
        ratingService.voteOnRating(r1.getId(), proUser.getId(), true);   // Helpful
        ratingService.voteOnRating(r1.getId(), masterUser.getId(), true); // Helpful
        System.out.println("👍 " + proUser.getUsername() + " marked " + noviceUser.getUsername() + "'s review as helpful");
        System.out.println("👍 " + masterUser.getUsername() + " marked " + noviceUser.getUsername() + "'s review as helpful");
        
        System.out.println();
        
        // Show user progression
        System.out.println("=== User Level Status ===\n");
        userService.getAllUsers().forEach(u -> {
            System.out.printf("   %s: %s (Ratings: %d, Helpful votes received: %d)%n",
                u.getUsername(), u.getLevel(), u.getTotalRatingsGiven(), u.getHelpfulVotesReceived());
        });
        
        // Show statistics
        System.out.println("\n=== System Statistics ===\n");
        System.out.println(ratingSystem.getStatisticsObserver());
        
        System.out.println("\n" + "=".repeat(60) + "\n");
    }
    
    private static void showAggregatedRatings() {
        movieService.getAllMovies().forEach(movie -> {
            double aggregatedRating = ratingService.getAggregatedRating(movie.getId());
            List<Rating> ratings = ratingService.getRatingsForMovie(movie.getId());
            
            System.out.printf("🎬 %s%n", movie.getTitle());
            System.out.printf("   Weighted Rating: %.2f/5.0 ⭐ (%d reviews)%n", aggregatedRating, ratings.size());
            System.out.printf("   Weight breakdown:%n");
            
            for (Rating rating : ratings) {
                userService.getUserById(rating.getUserId()).ifPresent(user -> {
                    System.out.printf("      - %s [%s, weight: %dx]: %s%n",
                        user.getUsername(),
                        user.getLevel().getDisplayName(),
                        user.getLevel().getWeightMultiplier(),
                        rating.getRatingValue());
                });
            }
            System.out.println();
        });
    }
    
    private static void runInteractiveMode() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;
        
        while (running) {
            System.out.println("\n=== Interactive Menu ===");
            System.out.println("1. View all movies with ratings");
            System.out.println("2. View all users");
            System.out.println("3. Add a new rating");
            System.out.println("4. Vote on a rating");
            System.out.println("5. Add new user");
            System.out.println("6. Add new movie");
            System.out.println("7. Simulate user progression");
            System.out.println("8. Show statistics");
            System.out.println("0. Exit");
            System.out.print("\nChoice: ");
            
            try {
                String input = scanner.nextLine().trim();
                if (input.isEmpty()) continue;
                
                int choice = Integer.parseInt(input);
                
                switch (choice) {
                    case 1 -> showAggregatedRatings();
                    case 2 -> showAllUsers();
                    case 3 -> addRatingInteractive(scanner);
                    case 4 -> voteOnRatingInteractive(scanner);
                    case 5 -> addUserInteractive(scanner);
                    case 6 -> addMovieInteractive(scanner);
                    case 7 -> simulateUserProgression(scanner);
                    case 8 -> showStatistics();
                    case 0 -> running = false;
                    default -> System.out.println("Invalid choice!");
                }
            } catch (NumberFormatException e) {
                System.out.println("Please enter a valid number.");
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
        
        System.out.println("\nThank you for using the Movie Rating System! 🎬");
        scanner.close();
    }
    
    private static void showAllUsers() {
        System.out.println("\n=== All Users ===\n");
        int i = 1;
        for (User user : userService.getAllUsers()) {
            System.out.printf("%d. %s%n", i++, user);
        }
    }
    
    private static void addRatingInteractive(Scanner scanner) {
        System.out.println("\n=== Add Rating ===");
        
        // Show users
        System.out.println("\nAvailable users:");
        List<User> users = userService.getAllUsers();
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, users.get(i).getUsername());
        }
        System.out.print("Select user (number): ");
        int userIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        User selectedUser = users.get(userIdx);
        
        // Show movies
        System.out.println("\nAvailable movies:");
        List<Movie> movies = movieService.getAllMovies();
        for (int i = 0; i < movies.size(); i++) {
            Movie m = movies.get(i);
            boolean alreadyRated = ratingService.hasUserRatedMovie(selectedUser.getId(), m.getId());
            System.out.printf("%d. %s %s%n", i + 1, m.getTitle(), alreadyRated ? "(already rated)" : "");
        }
        System.out.print("Select movie (number): ");
        int movieIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        Movie selectedMovie = movies.get(movieIdx);
        
        // Get rating value
        System.out.print("Rating (1-5 stars): ");
        int stars = Integer.parseInt(scanner.nextLine().trim());
        RatingValue ratingValue = RatingValue.fromInt(stars);
        
        // Get review
        System.out.print("Review (optional): ");
        String review = scanner.nextLine().trim();
        
        Rating rating = ratingService.createRating(
            selectedUser.getId(), 
            selectedMovie.getId(), 
            ratingValue, 
            review
        );
        
        System.out.printf("\n✅ Rating added: %s rated '%s' as %s%n", 
            selectedUser.getUsername(), selectedMovie.getTitle(), rating.getRatingValue());
    }
    
    private static void voteOnRatingInteractive(Scanner scanner) {
        System.out.println("\n=== Vote on Rating ===");
        
        // Show movies with ratings
        List<Movie> movies = movieService.getAllMovies();
        System.out.println("\nSelect a movie:");
        for (int i = 0; i < movies.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, movies.get(i).getTitle());
        }
        System.out.print("Choice: ");
        int movieIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        Movie selectedMovie = movies.get(movieIdx);
        
        // Show ratings for the movie
        List<Rating> ratings = ratingService.getRatingsForMovie(selectedMovie.getId());
        if (ratings.isEmpty()) {
            System.out.println("No ratings for this movie yet.");
            return;
        }
        
        System.out.println("\nRatings:");
        for (int i = 0; i < ratings.size(); i++) {
            Rating r = ratings.get(i);
            User author = userService.getUserById(r.getUserId()).orElse(null);
            System.out.printf("%d. %s by %s - \"%s\" (Helpful: %d/%d)%n", 
                i + 1, r.getRatingValue(), 
                author != null ? author.getUsername() : "Unknown",
                r.getReview().substring(0, Math.min(40, r.getReview().length())),
                r.getHelpfulVotes(), r.getHelpfulVotes() + r.getNotHelpfulVotes());
        }
        System.out.print("Select rating to vote on: ");
        int ratingIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        Rating selectedRating = ratings.get(ratingIdx);
        
        // Select voter
        List<User> users = userService.getAllUsers();
        System.out.println("\nWho is voting?");
        for (int i = 0; i < users.size(); i++) {
            System.out.printf("%d. %s%n", i + 1, users.get(i).getUsername());
        }
        System.out.print("Select voter: ");
        int voterIdx = Integer.parseInt(scanner.nextLine().trim()) - 1;
        User voter = users.get(voterIdx);
        
        System.out.print("Is this review helpful? (y/n): ");
        boolean isHelpful = scanner.nextLine().trim().toLowerCase().startsWith("y");
        
        ratingService.voteOnRating(selectedRating.getId(), voter.getId(), isHelpful);
        System.out.println("✅ Vote recorded!");
    }
    
    private static void addUserInteractive(Scanner scanner) {
        System.out.println("\n=== Add New User ===");
        
        System.out.print("Username: ");
        String username = scanner.nextLine().trim();
        
        System.out.print("Email: ");
        String email = scanner.nextLine().trim();
        
        User user = userService.registerUser(UserFactory.createUser(username, email));
        System.out.println("✅ User created: " + user);
    }
    
    private static void addMovieInteractive(Scanner scanner) {
        System.out.println("\n=== Add New Movie ===");
        
        System.out.print("Title: ");
        String title = scanner.nextLine().trim();
        
        System.out.print("Director: ");
        String director = scanner.nextLine().trim();
        
        System.out.print("Release Year: ");
        int year = Integer.parseInt(scanner.nextLine().trim());
        
        System.out.print("Genre: ");
        String genre = scanner.nextLine().trim();
        
        System.out.print("Description: ");
        String description = scanner.nextLine().trim();
        
        Movie movie = movieService.addMovie(
            MovieFactory.createMovie(title, director, year, genre, description)
        );
        System.out.println("✅ Movie added: " + movie);
    }
    
    private static void simulateUserProgression(Scanner scanner) {
        System.out.println("\n=== Simulate User Progression ===");
        System.out.println("This will simulate a user rating multiple movies to show level progression.");
        
        // Create a test user
        User testUser = userService.registerUser(
            UserFactory.createUser("test_progression_" + System.currentTimeMillis() % 1000, "test@email.com")
        );
        System.out.println("\nCreated test user: " + testUser.getUsername() + " [" + testUser.getLevel() + "]");
        
        // Get all movies
        List<Movie> movies = movieService.getAllMovies();
        
        // Add more movies if needed
        while (movies.size() < 15) {
            Movie m = movieService.addMovie(MovieFactory.createMovie(
                "Test Movie " + (movies.size() + 1), 2020 + movies.size()));
            movies = movieService.getAllMovies();
        }
        
        System.out.println("\nSimulating ratings... Press Enter after each to see progression:");
        
        for (int i = 0; i < Math.min(15, movies.size()); i++) {
            Movie movie = movies.get(i);
            if (!ratingService.hasUserRatedMovie(testUser.getId(), movie.getId())) {
                RatingValue value = RatingValue.values()[(int)(Math.random() * 5)];
                ratingService.createRating(testUser.getId(), movie.getId(), value, "Review " + (i+1));
                System.out.printf("Rated %d movies - Current level: %s (Total ratings: %d)%n",
                    i + 1, testUser.getLevel(), testUser.getTotalRatingsGiven());
            }
        }
        
        System.out.println("\nFinal user state: " + testUser);
    }
    
    private static void showStatistics() {
        System.out.println("\n=== System Statistics ===\n");
        System.out.println(ratingSystem.getStatisticsObserver());
        
        System.out.println("\nUsers by level:");
        for (UserLevel level : UserLevel.values()) {
            List<User> usersAtLevel = userService.getUsersByLevel(level);
            System.out.printf("  %s: %d users%n", level.getDisplayName(), usersAtLevel.size());
        }
    }
}


