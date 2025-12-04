package cricinfo.strategies.search;

import cricinfo.models.Player;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Search strategy for players using name matching.
 */
public class PlayerSearchStrategy implements SearchStrategy<Player> {
    
    @Override
    public List<Player> search(List<Player> players, String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        return players.stream()
                .filter(player -> matchesPlayer(player, lowerQuery))
                .collect(Collectors.toList());
    }
    
    private boolean matchesPlayer(Player player, String query) {
        // Match by name
        if (player.getName().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by country
        if (player.getCountry() != null && 
            player.getCountry().toLowerCase().contains(query)) {
            return true;
        }
        
        // Match by role
        if (player.getRole() != null && 
            player.getRole().getDisplayName().toLowerCase().contains(query)) {
            return true;
        }
        
        return false;
    }
    
    @Override
    public String getStrategyName() {
        return "PlayerNameSearchStrategy";
    }
}



