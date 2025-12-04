package linkedin.strategies.search;

import linkedin.models.SearchResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ranking strategy that prioritizes connections and their network.
 * Useful for people search where connections are more relevant.
 */
public class ConnectionBasedRankingStrategy implements SearchRankingStrategy {
    
    private static final double CONNECTION_BOOST = 20.0;
    private static final double RELEVANCE_WEIGHT = 0.6;
    private static final double CONNECTION_WEIGHT = 0.4;
    
    private final RelevanceRankingStrategy relevanceStrategy = new RelevanceRankingStrategy();
    
    @Override
    public List<SearchResult> rank(List<SearchResult> results, SearchContext context) {
        // First apply relevance ranking
        relevanceStrategy.rank(results, context);
        
        // Then boost based on connection status
        for (SearchResult result : results) {
            double relevanceScore = result.getRelevanceScore();
            double connectionBoost = 0.0;
            
            if (result.getType() == SearchResult.ResultType.USER) {
                if (context.isConnection(result.getId())) {
                    connectionBoost = CONNECTION_BOOST;
                }
            }
            
            // Combine scores with weights
            double finalScore = (relevanceScore * RELEVANCE_WEIGHT) + 
                               (connectionBoost * CONNECTION_WEIGHT);
            result.setRelevanceScore(finalScore);
        }
        
        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getRelevanceScore).reversed())
                .collect(Collectors.toList());
    }
}



