package movierating.strategies.aggregation;

import movierating.models.Rating;
import movierating.models.User;
import movierating.strategies.weight.WeightCalculationStrategy;

import java.util.List;
import java.util.Map;

/**
 * Bayesian average aggregation strategy.
 * Considers both weighted ratings and prior belief (average rating across all movies).
 * This prevents movies with few ratings from having extreme scores.
 * 
 * Formula: (W*v + m*C) / (W + m)
 * Where:
 * - W = total weight of ratings
 * - v = weighted average of ratings
 * - m = minimum weight threshold (prior strength)
 * - C = prior mean (global average rating)
 */
public class BayesianAverageStrategy implements RatingAggregationStrategy {
    
    private final WeightCalculationStrategy weightStrategy;
    private final double priorMean; // Global average rating
    private final double priorStrength; // Minimum weight threshold
    
    public BayesianAverageStrategy(WeightCalculationStrategy weightStrategy, 
                                    double priorMean, double priorStrength) {
        this.weightStrategy = weightStrategy;
        this.priorMean = priorMean;
        this.priorStrength = priorStrength;
    }
    
    /**
     * Create with default prior values.
     */
    public BayesianAverageStrategy(WeightCalculationStrategy weightStrategy) {
        this(weightStrategy, 3.0, 10.0); // Default: 3.0 mean, 10.0 strength
    }
    
    @Override
    public double calculateAggregateRating(List<Rating> ratings, Map<String, User> userMap) {
        if (ratings == null || ratings.isEmpty()) {
            return priorMean; // Return prior mean when no ratings
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
        
        // Bayesian average formula
        return (weightedSum + priorStrength * priorMean) / (totalWeight + priorStrength);
    }
    
    @Override
    public String getDescription() {
        return String.format("Bayesian average (prior=%.1f, strength=%.1f) using: %s", 
                priorMean, priorStrength, weightStrategy.getDescription());
    }
}


