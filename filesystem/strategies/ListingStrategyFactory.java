package filesystem.strategies;

import java.util.HashMap;
import java.util.Map;

/**
 * Factory for creating and managing listing strategies.
 * Supports registration of custom strategies for extensibility.
 */
public class ListingStrategyFactory {
    
    private static final String DEFAULT_STRATEGY = "simple";
    private static final String DETAILED_STRATEGY = "detailed";
    
    private final Map<String, ListingStrategy> strategies;
    
    public ListingStrategyFactory() {
        this.strategies = new HashMap<>();
        registerDefaultStrategies();
    }
    
    private void registerDefaultStrategies() {
        register(new SimpleListingStrategy());
        register(new DetailedListingStrategy());
    }
    
    /**
     * Registers a custom listing strategy.
     * 
     * @param strategy The strategy to register
     */
    public void register(ListingStrategy strategy) {
        strategies.put(strategy.getName(), strategy);
    }
    
    /**
     * Gets a strategy by name.
     * 
     * @param name The strategy name
     * @return The strategy, or default if not found
     */
    public ListingStrategy getStrategy(String name) {
        return strategies.getOrDefault(name, strategies.get(DEFAULT_STRATEGY));
    }
    
    /**
     * Gets the default (simple) listing strategy.
     */
    public ListingStrategy getDefaultStrategy() {
        return strategies.get(DEFAULT_STRATEGY);
    }
    
    /**
     * Gets the detailed listing strategy.
     */
    public ListingStrategy getDetailedStrategy() {
        return strategies.get(DETAILED_STRATEGY);
    }
}

