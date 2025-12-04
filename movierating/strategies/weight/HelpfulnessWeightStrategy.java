package movierating.strategies.weight;

import movierating.models.Rating;
import movierating.models.User;

/**
 * Weight calculation strategy that combines user level with rating helpfulness.
 * Ratings that are marked as helpful by other users get additional weight.
 */
public class HelpfulnessWeightStrategy implements WeightCalculationStrategy {
    
    private static final double HELPFULNESS_BONUS_MULTIPLIER = 1.5;
    private static final int MIN_VOTES_FOR_BONUS = 5;
    
    @Override
    public double calculateWeight(User user, Rating rating) {
        double baseWeight = user.getLevel().getWeightMultiplier();
        
        // Apply helpfulness bonus if the rating has enough votes and is considered helpful
        int totalVotes = rating.getHelpfulVotes() + rating.getNotHelpfulVotes();
        if (totalVotes >= MIN_VOTES_FOR_BONUS) {
            double helpfulnessScore = rating.getHelpfulnessScore();
            if (helpfulnessScore > 0.6) {
                // Apply bonus based on how helpful the rating is
                baseWeight *= (1 + (helpfulnessScore - 0.5) * HELPFULNESS_BONUS_MULTIPLIER);
            }
        }
        
        return baseWeight;
    }
    
    @Override
    public String getDescription() {
        return "Weight based on user level with helpfulness bonus";
    }
}


