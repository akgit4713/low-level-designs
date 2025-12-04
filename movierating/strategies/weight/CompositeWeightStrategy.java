package movierating.strategies.weight;

import movierating.models.Rating;
import movierating.models.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Composite weight strategy that combines multiple weight calculation strategies.
 * 
 * Composite Pattern: Treats group of strategies as a single strategy.
 * Open/Closed Principle: New strategies can be added without modifying this class.
 */
public class CompositeWeightStrategy implements WeightCalculationStrategy {
    
    private final List<WeightCalculationStrategy> strategies;
    private final CombinationMethod combinationMethod;
    
    public enum CombinationMethod {
        AVERAGE,
        MULTIPLY,
        MAX
    }
    
    public CompositeWeightStrategy(CombinationMethod method) {
        this.strategies = new ArrayList<>();
        this.combinationMethod = method;
    }
    
    public void addStrategy(WeightCalculationStrategy strategy) {
        strategies.add(strategy);
    }
    
    public void removeStrategy(WeightCalculationStrategy strategy) {
        strategies.remove(strategy);
    }
    
    @Override
    public double calculateWeight(User user, Rating rating) {
        if (strategies.isEmpty()) {
            return 1.0; // Default weight
        }
        
        return switch (combinationMethod) {
            case AVERAGE -> calculateAverage(user, rating);
            case MULTIPLY -> calculateProduct(user, rating);
            case MAX -> calculateMax(user, rating);
        };
    }
    
    private double calculateAverage(User user, Rating rating) {
        double sum = 0;
        for (WeightCalculationStrategy strategy : strategies) {
            sum += strategy.calculateWeight(user, rating);
        }
        return sum / strategies.size();
    }
    
    private double calculateProduct(User user, Rating rating) {
        double product = 1.0;
        for (WeightCalculationStrategy strategy : strategies) {
            product *= strategy.calculateWeight(user, rating);
        }
        return Math.pow(product, 1.0 / strategies.size()); // Geometric mean
    }
    
    private double calculateMax(User user, Rating rating) {
        double max = 0;
        for (WeightCalculationStrategy strategy : strategies) {
            max = Math.max(max, strategy.calculateWeight(user, rating));
        }
        return max;
    }
    
    @Override
    public String getDescription() {
        StringBuilder sb = new StringBuilder("Composite strategy (");
        sb.append(combinationMethod).append("): [");
        for (int i = 0; i < strategies.size(); i++) {
            sb.append(strategies.get(i).getDescription());
            if (i < strategies.size() - 1) {
                sb.append(", ");
            }
        }
        sb.append("]");
        return sb.toString();
    }
}


