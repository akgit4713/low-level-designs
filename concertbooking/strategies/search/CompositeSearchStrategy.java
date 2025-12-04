package concertbooking.strategies.search;

import concertbooking.models.Concert;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Composite search strategy that combines multiple search strategies
 */
public class CompositeSearchStrategy implements SearchStrategy {
    
    private final List<SearchStrategy> strategies = new ArrayList<>();
    private final boolean matchAll; // AND vs OR logic
    
    public CompositeSearchStrategy(boolean matchAll) {
        this.matchAll = matchAll;
    }
    
    public void addStrategy(SearchStrategy strategy) {
        strategies.add(strategy);
    }
    
    public void removeStrategy(SearchStrategy strategy) {
        strategies.remove(strategy);
    }
    
    @Override
    public List<Concert> search(List<Concert> concerts, String query) {
        if (strategies.isEmpty()) {
            return concerts;
        }
        
        if (matchAll) {
            // AND logic: concert must match all strategies
            List<Concert> result = new ArrayList<>(concerts);
            for (SearchStrategy strategy : strategies) {
                result = strategy.search(result, query);
            }
            return result;
        } else {
            // OR logic: concert must match at least one strategy
            return concerts.stream()
                .filter(concert -> strategies.stream()
                    .anyMatch(strategy -> !strategy.search(List.of(concert), query).isEmpty()))
                .distinct()
                .collect(Collectors.toList());
        }
    }
    
    @Override
    public String getStrategyName() {
        return "Composite Search (" + (matchAll ? "AND" : "OR") + ")";
    }
}



