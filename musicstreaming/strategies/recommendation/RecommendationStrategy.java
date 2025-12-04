package musicstreaming.strategies.recommendation;

import musicstreaming.models.Song;
import musicstreaming.models.User;
import java.util.List;

/**
 * Strategy interface for different recommendation algorithms.
 * Implements the Strategy pattern for extensible recommendations.
 */
public interface RecommendationStrategy {
    
    /**
     * Generate song recommendations for a user.
     * 
     * @param user The user to generate recommendations for
     * @param limit Maximum number of recommendations to return
     * @return List of recommended songs
     */
    List<Song> recommend(User user, int limit);
    
    /**
     * Get the name/type of this recommendation strategy.
     */
    String getStrategyName();
    
    /**
     * Get a description of how this strategy works.
     */
    String getDescription();
}



