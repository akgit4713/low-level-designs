package linkedin.strategies.search;

import linkedin.models.SearchResult;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Default ranking strategy based on text relevance.
 * Calculates relevance score based on query match in title and description.
 */
public class RelevanceRankingStrategy implements SearchRankingStrategy {
    
    @Override
    public List<SearchResult> rank(List<SearchResult> results, SearchContext context) {
        String query = context.getQuery().toLowerCase();
        String[] queryTerms = query.split("\\s+");
        
        for (SearchResult result : results) {
            double score = calculateRelevanceScore(result, queryTerms);
            result.setRelevanceScore(score);
        }
        
        return results.stream()
                .sorted(Comparator.comparingDouble(SearchResult::getRelevanceScore).reversed())
                .collect(Collectors.toList());
    }
    
    private double calculateRelevanceScore(SearchResult result, String[] queryTerms) {
        double score = 0.0;
        String title = result.getTitle() != null ? result.getTitle().toLowerCase() : "";
        String subtitle = result.getSubtitle() != null ? result.getSubtitle().toLowerCase() : "";
        String description = result.getDescription() != null ? result.getDescription().toLowerCase() : "";
        
        for (String term : queryTerms) {
            // Title match has highest weight
            if (title.contains(term)) {
                score += 10.0;
                // Exact title match bonus
                if (title.equals(term)) {
                    score += 5.0;
                }
            }
            
            // Subtitle match has medium weight
            if (subtitle.contains(term)) {
                score += 5.0;
            }
            
            // Description match has lower weight
            if (description.contains(term)) {
                score += 2.0;
            }
        }
        
        return score;
    }
}



