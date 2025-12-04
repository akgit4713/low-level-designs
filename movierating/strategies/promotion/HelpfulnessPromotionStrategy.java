package movierating.strategies.promotion;

import movierating.models.User;
import movierating.models.UserLevel;

/**
 * Promotion strategy based on how helpful a user's ratings are to others.
 * Users are promoted/demoted based on helpful votes received.
 */
public class HelpfulnessPromotionStrategy implements LevelPromotionStrategy {
    
    private static final int INTERMEDIATE_THRESHOLD = 20;
    private static final int PRO_THRESHOLD = 100;
    private static final int EXPERT_THRESHOLD = 300;
    private static final int MASTER_THRESHOLD = 1000;
    
    private static final double DEMOTION_RATIO_THRESHOLD = 0.3;
    private static final int MIN_RATINGS_FOR_DEMOTION = 20;
    
    @Override
    public UserLevel evaluateLevel(User user) {
        int helpfulVotes = user.getHelpfulVotesReceived();
        
        if (helpfulVotes >= MASTER_THRESHOLD) {
            return UserLevel.MASTER;
        } else if (helpfulVotes >= EXPERT_THRESHOLD) {
            return UserLevel.EXPERT;
        } else if (helpfulVotes >= PRO_THRESHOLD) {
            return UserLevel.PRO;
        } else if (helpfulVotes >= INTERMEDIATE_THRESHOLD) {
            return UserLevel.INTERMEDIATE;
        } else {
            return UserLevel.NOVICE;
        }
    }
    
    @Override
    public boolean shouldPromote(User user) {
        UserLevel newLevel = evaluateLevel(user);
        return newLevel.ordinal() > user.getLevel().ordinal();
    }
    
    @Override
    public boolean shouldDemote(User user) {
        // Demote if user has given many ratings but received few helpful votes
        int totalRatings = user.getTotalRatingsGiven();
        if (totalRatings < MIN_RATINGS_FOR_DEMOTION) {
            return false;
        }
        
        double helpfulnessRatio = (double) user.getHelpfulVotesReceived() / totalRatings;
        
        // If helpfulness ratio is too low, consider demotion
        if (helpfulnessRatio < DEMOTION_RATIO_THRESHOLD && user.getLevel() != UserLevel.NOVICE) {
            UserLevel expectedLevel = evaluateLevel(user);
            return expectedLevel.ordinal() < user.getLevel().ordinal();
        }
        
        return false;
    }
    
    @Override
    public String getDescription() {
        return "Promotion based on helpful votes received";
    }
}


