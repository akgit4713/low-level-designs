package movierating.observers;

import movierating.models.Rating;
import movierating.models.User;
import movierating.models.UserLevel;
import movierating.strategies.promotion.LevelPromotionStrategy;

/**
 * Observer that updates user levels based on their rating activity.
 * 
 * Observer Pattern: Reacts to rating events to update user levels.
 * Single Responsibility: Only handles user level updates based on rating events.
 */
public class UserLevelUpdateObserver implements RatingObserver {
    
    private final LevelPromotionStrategy promotionStrategy;
    private final LevelChangeListener levelChangeListener;
    
    /**
     * Callback interface for level changes.
     */
    public interface LevelChangeListener {
        void onLevelChanged(User user, UserLevel oldLevel, UserLevel newLevel);
    }
    
    public UserLevelUpdateObserver(LevelPromotionStrategy promotionStrategy, 
                                    LevelChangeListener levelChangeListener) {
        this.promotionStrategy = promotionStrategy;
        this.levelChangeListener = levelChangeListener;
    }
    
    public UserLevelUpdateObserver(LevelPromotionStrategy promotionStrategy) {
        this(promotionStrategy, null);
    }
    
    @Override
    public void onRatingCreated(Rating rating, User user) {
        evaluateAndUpdateLevel(user);
    }
    
    @Override
    public void onRatingUpdated(Rating rating, User user) {
        // Rating updates don't typically affect level
    }
    
    @Override
    public void onRatingDeleted(Rating rating, User user) {
        // Could implement demotion on rating deletion if needed
    }
    
    @Override
    public void onRatingVoted(Rating rating, User ratingAuthor, User voter, boolean isHelpful) {
        // Update the rating author's level based on helpful votes
        evaluateAndUpdateLevel(ratingAuthor);
    }
    
    /**
     * Evaluate and potentially update a user's level.
     */
    private void evaluateAndUpdateLevel(User user) {
        UserLevel oldLevel = user.getLevel();
        UserLevel newLevel = promotionStrategy.evaluateLevel(user);
        
        if (newLevel != oldLevel) {
            user.setLevel(newLevel);
            if (levelChangeListener != null) {
                levelChangeListener.onLevelChanged(user, oldLevel, newLevel);
            }
        }
    }
}


