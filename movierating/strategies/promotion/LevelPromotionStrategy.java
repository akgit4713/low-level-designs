package movierating.strategies.promotion;

import movierating.models.User;
import movierating.models.UserLevel;

/**
 * Strategy interface for determining user level promotions/demotions.
 * 
 * Strategy Pattern: Allows different promotion criteria to be used interchangeably.
 * Open/Closed Principle: New promotion strategies can be added without modifying existing code.
 */
public interface LevelPromotionStrategy {
    
    /**
     * Evaluate if the user should be promoted or demoted.
     * 
     * @param user The user to evaluate
     * @return The new level for the user (may be same as current)
     */
    UserLevel evaluateLevel(User user);
    
    /**
     * Check if user qualifies for promotion.
     * @param user The user to check
     * @return true if user qualifies for promotion
     */
    boolean shouldPromote(User user);
    
    /**
     * Check if user should be demoted.
     * @param user The user to check
     * @return true if user should be demoted
     */
    boolean shouldDemote(User user);
    
    /**
     * Get a description of this promotion strategy.
     * @return Description string
     */
    String getDescription();
}


