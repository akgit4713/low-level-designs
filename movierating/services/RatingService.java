package movierating.services;

import movierating.models.Movie;
import movierating.models.Rating;
import movierating.models.RatingValue;
import movierating.models.User;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for rating operations.
 * 
 * Interface Segregation: Only rating-related operations.
 * Dependency Inversion: High-level modules depend on this abstraction.
 */
public interface RatingService {
    
    /**
     * Create a new rating for a movie.
     * @param userId The user giving the rating
     * @param movieId The movie being rated
     * @param ratingValue The rating value
     * @param review Optional review text
     * @return The created rating
     */
    Rating createRating(String userId, String movieId, RatingValue ratingValue, String review);
    
    /**
     * Update an existing rating.
     * @param ratingId The rating ID to update
     * @param newValue The new rating value
     * @param newReview The new review text
     * @return The updated rating
     */
    Rating updateRating(String ratingId, RatingValue newValue, String newReview);
    
    /**
     * Get a rating by ID.
     * @param ratingId The rating ID
     * @return Optional containing the rating if found
     */
    Optional<Rating> getRatingById(String ratingId);
    
    /**
     * Get all ratings for a movie.
     * @param movieId The movie ID
     * @return List of ratings for the movie
     */
    List<Rating> getRatingsForMovie(String movieId);
    
    /**
     * Get all ratings by a user.
     * @param userId The user ID
     * @return List of ratings by the user
     */
    List<Rating> getRatingsByUser(String userId);
    
    /**
     * Get the aggregated rating for a movie.
     * @param movieId The movie ID
     * @return The aggregated rating (0.0 to 5.0)
     */
    double getAggregatedRating(String movieId);
    
    /**
     * Mark a rating as helpful.
     * @param ratingId The rating ID
     * @param voterId The user voting
     * @param isHelpful true for helpful, false for not helpful
     */
    void voteOnRating(String ratingId, String voterId, boolean isHelpful);
    
    /**
     * Delete a rating.
     * @param ratingId The rating ID to delete
     * @return true if rating was deleted
     */
    boolean deleteRating(String ratingId);
    
    /**
     * Check if a user has already rated a movie.
     * @param userId The user ID
     * @param movieId The movie ID
     * @return true if user has rated the movie
     */
    boolean hasUserRatedMovie(String userId, String movieId);
    
    /**
     * Get the user's rating for a specific movie.
     * @param userId The user ID
     * @param movieId The movie ID
     * @return Optional containing the rating if found
     */
    Optional<Rating> getUserRatingForMovie(String userId, String movieId);
}


