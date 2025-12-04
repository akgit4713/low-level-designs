package movierating.strategies.aggregation;

import movierating.models.Rating;
import movierating.models.User;
import movierating.strategies.weight.WeightCalculationStrategy;

import java.util.List;
import java.util.Map;

/**
 * Weighted average aggregation strategy.
 * Uses a weight calculation strategy to determine the impact of each rating.
 * 
 * Dependency Injection: WeightCalculationStrategy is injected, making this class flexible.
 * Single Responsibility: This class only aggregates; weight calculation is delegated.
 */
public class WeightedAverageStrategy implements RatingAggregationStrategy {
    
    private final WeightCalculationStrategy weightStrategy;
    
    public WeightedAverageStrategy(WeightCalculationStrategy weightStrategy) {
        this.weightStrategy = weightStrategy;
    }
    
    @Override
    public double calculateAggregateRating(List<Rating> ratings, Map<String, User> userMap) {
        if (ratings == null || ratings.isEmpty()) {
            return 0.0;
        }
        
        double weightedSum = 0;
        double totalWeight = 0;
        
        for (Rating rating : ratings) {
            User user = userMap.get(rating.getUserId());
            if (user != null) {
                double weight = weightStrategy.calculateWeight(user, rating);
                weightedSum += rating.getRatingValue().getValue() * weight;
                totalWeight += weight;
            }
        }
        
        if (totalWeight == 0) {
            return 0.0;
        }
        
        return weightedSum / totalWeight;
    }
    
    @Override
    public String getDescription() {
        return "Weighted average using: " + weightStrategy.getDescription();
    }
}


