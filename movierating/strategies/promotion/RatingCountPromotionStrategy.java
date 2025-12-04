package movierating.strategies.promotion;

import movierating.models.User;
import movierating.models.UserLevel;

/**
 * Promotion strategy based on the number of ratings given by the user.
 * Users are promoted when they reach certain rating count thresholds.
 */
public class RatingCountPromotionStrategy implements LevelPromotionStrategy {
    
    @Override
    public UserLevel evaluateLevel(User user) {
        int ratingsCount = user.getTotalRatingsGiven();
        
        // Find the highest level the user qualifies for based on rating count
        UserLevel[] levels = UserLevel.values();
        UserLevel qualifiedLevel = UserLevel.NOVICE;
        
        for (UserLevel level : levels) {
            if (ratingsCount >= level.getMinRatingsRequired()) {
                qualifiedLevel = level;
            } else {
                break;
            }
        }
        
        return qualifiedLevel;
    }
    
    @Override
    public boolean shouldPromote(User user) {
        UserLevel newLevel = evaluateLevel(user);
        return newLevel.ordinal() > user.getLevel().ordinal();
    }
    
    @Override
    public boolean shouldDemote(User user) {
        // In rating count strategy, users are not demoted
        // (ratings count can only go up)
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Promotion based on total ratings count";
    }
}


