package movierating.factories;

import movierating.models.Movie;

/**
 * Factory for creating Movie objects.
 * 
 * Factory Pattern: Encapsulates movie creation logic.
 * Single Responsibility: Only handles movie object creation.
 */
public class MovieFactory {
    
    /**
     * Create a new movie with all details.
     * @param title The movie title
     * @param director The director name
     * @param releaseYear The release year
     * @param genre The genre
     * @param description The description
     * @return The created movie
     */
    public static Movie createMovie(String title, String director, int releaseYear, 
                                     String genre, String description) {
        return new Movie(title, director, releaseYear, genre, description);
    }
    
    /**
     * Create a movie with minimal info.
     * @param title The movie title
     * @param releaseYear The release year
     * @return The created movie
     */
    public static Movie createMovie(String title, int releaseYear) {
        return new Movie(title, "Unknown", releaseYear, "Unspecified", "");
    }
}


