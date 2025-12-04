package linkedin.strategies.search;

import linkedin.models.SearchResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Ranking strategy that prioritizes results in the same location.
 * Useful for job searches where location matters.
 */
public class LocationBasedRankingStrategy implements SearchRankingStrategy {
    
    private static final double LOCATION_MATCH_BOOST = 15.0;
    private static final double RELEVANCE_WEIGHT = 0.7;
    private static final double LOCATION_WEIGHT = 0.3;
    
    private final RelevanceRankingStrategy relevanceStrategy = new RelevanceRankingStrategy();
    
    @Override
    public List<SearchResult> rank(List<SearchResult> results, SearchContext context) {
        // First apply relevance ranking
        relevanceStrategy.rank(results, context);
        
        String searcherLocation = context.getSearcherLocation();
        
        // Then boost based on location match
        for (SearchResult result : results) {
            double relevanceScore = result.getRelevanceScore();
            double locationBoost = 0.0;
            
            if (searcherLocation != null && result.getSubtitle() != null) {
                // Subtitle typically contains location info
                if (result.getSubtitle().toLowerCase()
                        .contains(searcherLocation.toLowerCase())) {
                    locationBoost = LOCATION_MATCH_BOOST;
                }
            }
            
            double finalScore = (relevanceScore * RELEVANCE_WEIGHT) + 
                               (locationBoost * LOCATION_WEIGHT);
            result.setRelevanceScore(finalScore);
        }
        
        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getRelevanceScore).reversed())
                .collect(Collectors.toList());
    }
}



