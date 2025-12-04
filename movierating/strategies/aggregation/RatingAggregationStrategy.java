package movierating.strategies.aggregation;

import movierating.models.Rating;
import movierating.models.User;

import java.util.List;
import java.util.Map;

/**
 * Strategy interface for aggregating movie ratings.
 * 
 * Strategy Pattern: Allows different aggregation algorithms to be used interchangeably.
 * Open/Closed Principle: New aggregation strategies can be added without modifying existing code.
 */
public interface RatingAggregationStrategy {
    
    /**
     * Calculate the aggregate rating for a movie.
     * 
     * @param ratings List of ratings for the movie
     * @param userMap Map of user IDs to User objects for weight calculation
     * @return Aggregated rating value (typically 0.0 to 5.0)
     */
    double calculateAggregateRating(List<Rating> ratings, Map<String, User> userMap);
    
    /**
     * Get a description of this aggregation strategy.
     * @return Description string
     */
    String getDescription();
}


