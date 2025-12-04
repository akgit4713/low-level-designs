package splitwise.factories;

import splitwise.enums.SplitMethod;
import splitwise.strategies.*;

import java.util.EnumMap;
import java.util.Map;

/**
 * Factory for creating split strategy instances.
 * Supports registering new strategies at runtime.
 */
public class SplitStrategyFactory {
    
    private final Map<SplitMethod, SplitStrategy> strategies;
    
    public SplitStrategyFactory() {
        strategies = new EnumMap<>(SplitMethod.class);
        // Register default strategies
        strategies.put(SplitMethod.EQUAL, new EqualSplitStrategy());
        strategies.put(SplitMethod.PERCENTAGE, new PercentageSplitStrategy());
        strategies.put(SplitMethod.EXACT, new ExactSplitStrategy());
    }
    
    /**
     * Get the strategy for the given split method.
     */
    public SplitStrategy getStrategy(SplitMethod method) {
        SplitStrategy strategy = strategies.get(method);
        if (strategy == null) {
            throw new IllegalArgumentException("No strategy registered for: " + method);
        }
        return strategy;
    }
    
    /**
     * Register a new strategy for a split method.
     * Can be used to override existing strategies or add new ones.
     */
    public void register(SplitMethod method, SplitStrategy strategy) {
        strategies.put(method, strategy);
    }
    
    /**
     * Check if a strategy is registered for the given method.
     */
    public boolean hasStrategy(SplitMethod method) {
        return strategies.containsKey(method);
    }
}



