package cricinfo.strategies.search;

import cricinfo.models.Team;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for teams.
 */
public class TeamSearchStrategy implements SearchStrategy<Team> {
    
    @Override
    public List<Team> search(List<Team> teams, String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        return teams.stream()
                .filter(team -> matchesTeam(team, lowerQuery))
                .collect(Collectors.toList());
    }
    
    private boolean matchesTeam(Team team, String query) {
        // Match by name
        if (team.getName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by short name
        if (team.getShortName() != null && 
            team.getShortName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by country
        if (team.getCountry() != null && 
            team.getCountry().toLowerCase().contains(query)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "TeamSearchStrategy";
    }
}



