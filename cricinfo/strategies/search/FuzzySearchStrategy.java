package cricinfo.strategies.search;

import cricinfo.models.Player;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Fuzzy search strategy using Levenshtein distance for better matching.
 */
public class FuzzySearchStrategy implements SearchStrategy<Player> {
    
    private final int maxDistance;
    
    public FuzzySearchStrategy() {
        this.maxDistance = 3; // Default max edit distance
    }
    
    public FuzzySearchStrategy(int maxDistance) {
        this.maxDistance = maxDistance;
    }
    
    @Override
    public List<Player> search(List<Player> players, String query) {
        String lowerQuery = query.toLowerCase().trim();
        
        return players.stream()
                .map(player -> new ScoredPlayer(player, 
                        calculateScore(player.getName().toLowerCase(), lowerQuery)))
                .filter(sp -> sp.score <= maxDistance)
                .sorted(Comparator.comparingInt(sp -> sp.score))
                .map(sp -> sp.player)
                .collect(Collectors.toList());
    }
    
    private int calculateScore(String source, String target) {
        // If exact match or contains, give best score
        if (source.contains(target) || target.contains(source)) {
            return 0;
        }
        
        // Calculate Levenshtein distance
        return levenshteinDistance(source, target);
    }
    
    /**
     * Calculate Levenshtein (edit) distance between two strings.
     */
    private int levenshteinDistance(String s1, String s2) {
        int m = s1.length();
        int n = s2.length();
        
        int[][] dp = new int[m + 1][n + 1];
        
        for (int i = 0; i <= m; i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= n; j++) {
            dp[0][j] = j;
        }
        
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {
                int cost = (s1.charAt(i - 1) == s2.charAt(j - 1)) ? 0 : 1;
                dp[i][j] = Math.min(
                        Math.min(dp[i - 1][j] + 1, dp[i][j - 1] + 1),
                        dp[i - 1][j - 1] + cost
                );
            }
        }
        
        return dp[m][n];
    }
    
    @Override
    public String getStrategyName() {
        return "FuzzySearchStrategy";
    }
    
    private static class ScoredPlayer {
        final Player player;
        final int score;
        
        ScoredPlayer(Player player, int score) {
            this.player = player;
            this.score = score;
        }
    }
}



