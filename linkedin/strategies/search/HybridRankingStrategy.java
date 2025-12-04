package linkedin.strategies.search;

import linkedin.models.SearchResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Hybrid ranking strategy that combines multiple factors:
 * - Text relevance
 * - Connection status
 * - Location match
 * - Industry match
 */
public class HybridRankingStrategy implements SearchRankingStrategy {
    
    private static final double RELEVANCE_WEIGHT = 0.4;
    private static final double CONNECTION_WEIGHT = 0.25;
    private static final double LOCATION_WEIGHT = 0.2;
    private static final double INDUSTRY_WEIGHT = 0.15;
    
    private static final double CONNECTION_BOOST = 20.0;
    private static final double LOCATION_BOOST = 15.0;
    private static final double INDUSTRY_BOOST = 10.0;
    
    @Override
    public List<SearchResult> rank(List<SearchResult> results, SearchContext context) {
        String query = context.getQuery().toLowerCase();
        String[] queryTerms = query.split("\\s+");
        
        for (SearchResult result : results) {
            double relevanceScore = calculateRelevanceScore(result, queryTerms);
            double connectionScore = calculateConnectionScore(result, context);
            double locationScore = calculateLocationScore(result, context);
            double industryScore = calculateIndustryScore(result, context);
            
            double finalScore = (relevanceScore * RELEVANCE_WEIGHT) +
                               (connectionScore * CONNECTION_WEIGHT) +
                               (locationScore * LOCATION_WEIGHT) +
                               (industryScore * INDUSTRY_WEIGHT);
            
            result.setRelevanceScore(finalScore);
        }
        
        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getRelevanceScore).reversed())
                .collect(Collectors.toList());
    }
    
    private double calculateRelevanceScore(SearchResult result, String[] queryTerms) {
        double score = 0.0;
        String title = result.getTitle() != null ? result.getTitle().toLowerCase() : "";
        String description = result.getDescription() != null ? result.getDescription().toLowerCase() : "";
        
        for (String term : queryTerms) {
            if (title.contains(term)) score += 10.0;
            if (description.contains(term)) score += 3.0;
        }
        
        return score;
    }
    
    private double calculateConnectionScore(SearchResult result, SearchContext context) {
        if (result.getType() == SearchResult.ResultType.USER && 
            context.isConnection(result.getId())) {
            return CONNECTION_BOOST;
        }
        return 0.0;
    }
    
    private double calculateLocationScore(SearchResult result, SearchContext context) {
        String searcherLocation = context.getSearcherLocation();
        if (searcherLocation != null && result.getSubtitle() != null) {
            if (result.getSubtitle().toLowerCase().contains(searcherLocation.toLowerCase())) {
                return LOCATION_BOOST;
            }
        }
        return 0.0;
    }
    
    private double calculateIndustryScore(SearchResult result, SearchContext context) {
        String searcherIndustry = context.getSearcherIndustry();
        if (searcherIndustry != null && result.getDescription() != null) {
            if (result.getDescription().toLowerCase().contains(searcherIndustry.toLowerCase())) {
                return INDUSTRY_BOOST;
            }
        }
        return 0.0;
    }
}



