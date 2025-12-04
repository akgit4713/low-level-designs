package linkedin.strategies.search;

import linkedin.models.SearchResult;
import java.util.List;

/**
 * Strategy interface for ranking search results.
 * Different implementations can prioritize different factors.
 */
public interface SearchRankingStrategy {
    
    /**
     * Rank and sort search results based on the strategy's criteria
     * @param results The list of search results to rank
     * @param context The search context containing user info and query
     * @return Ranked list of search results
     */
    List<SearchResult> rank(List<SearchResult> results, SearchContext context);
}



