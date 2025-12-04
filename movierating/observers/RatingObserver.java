package movierating.observers;

import movierating.models.Rating;
import movierating.models.User;

/**
 * Observer interface for rating-related events.
 * 
 * Observer Pattern: Allows loose coupling between the rating system and
 * components that need to react to rating events.
 * 
 * Open/Closed Principle: New observers can be added without modifying the rating system.
 */
public interface RatingObserver {
    
    /**
     * Called when a new rating is created.
     * @param rating The new rating
     * @param user The user who created the rating
     */
    void onRatingCreated(Rating rating, User user);
    
    /**
     * Called when a rating is updated.
     * @param rating The updated rating
     * @param user The user who updated the rating
     */
    void onRatingUpdated(Rating rating, User user);
    
    /**
     * Called when a rating is deleted.
     * @param rating The deleted rating
     * @param user The user whose rating was deleted
     */
    void onRatingDeleted(Rating rating, User user);
    
    /**
     * Called when a rating receives a helpful vote.
     * @param rating The rating that received the vote
     * @param ratingAuthor The user who authored the rating
     * @param voter The user who cast the vote
     */
    void onRatingVoted(Rating rating, User ratingAuthor, User voter, boolean isHelpful);
}


