package cricinfo.strategies.search;

import cricinfo.models.Match;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for matches.
 */
public class MatchSearchStrategy implements SearchStrategy<Match> {
    
    @Override
    public List<Match> search(List<Match> matches, String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        return matches.stream()
                .filter(match -> matchesMatch(match, lowerQuery))
                .collect(Collectors.toList());
    }
    
    private boolean matchesMatch(Match match, String query) {
        // Match by title
        if (match.getTitle().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by series name
        if (match.getSeriesName() != null && 
            match.getSeriesName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by team names
        if (match.getTeam1().getName().toLowerCase().contains(query) ||
            match.getTeam2().getName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by venue
        if (match.getVenue() != null && 
            match.getVenue().getName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by format
        if (match.getFormat().getDisplayName().toLowerCase().contains(query)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "MatchSearchStrategy";
    }
}



