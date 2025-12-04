package movierating.strategies.weight;

import movierating.models.Rating;
import movierating.models.User;

/**
 * Strategy interface for calculating the weight of a rating.
 * 
 * Strategy Pattern: Allows different weight calculation algorithms to be used interchangeably.
 * Open/Closed Principle: New weight calculation strategies can be added without modifying existing code.
 * Dependency Inversion: High-level modules depend on this abstraction, not concrete implementations.
 */
public interface WeightCalculationStrategy {
    
    /**
     * Calculate the weight multiplier for a rating based on user and rating attributes.
     * 
     * @param user The user who gave the rating
     * @param rating The rating to calculate weight for
     * @return Weight multiplier (higher values = more impact on aggregate rating)
     */
    double calculateWeight(User user, Rating rating);
    
    /**
     * Get a description of this weight calculation strategy.
     * @return Description string
     */
    String getDescription();
}


