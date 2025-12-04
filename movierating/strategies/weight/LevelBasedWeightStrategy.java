package movierating.strategies.weight;

import movierating.models.Rating;
import movierating.models.User;

/**
 * Weight calculation strategy based purely on user level.
 * Higher level users have more weight in their ratings.
 */
public class LevelBasedWeightStrategy implements WeightCalculationStrategy {
    
    @Override
    public double calculateWeight(User user, Rating rating) {
        return user.getLevel().getWeightMultiplier();
    }
    
    @Override
    public String getDescription() {
        return "Weight based on user level only";
    }
}


