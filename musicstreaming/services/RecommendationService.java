package musicstreaming.services;

import musicstreaming.models.Playlist;
import musicstreaming.models.Song;
import java.util.List;

/**
 * Service interface for generating recommendations.
 */
public interface RecommendationService {
    
    /**
     * Get personalized song recommendations for a user.
     */
    List<Song> getRecommendedSongs(String userId, int limit);
    
    /**
     * Get recommended songs based on a specific strategy.
     */
    List<Song> getRecommendedSongs(String userId, String strategyName, int limit);
    
    /**
     * Get recommended playlists for a user.
     */
    List<Playlist> getRecommendedPlaylists(String userId, int limit);
    
    /**
     * Generate a personalized "Discover Weekly" style playlist.
     */
    Playlist generateDiscoverWeekly(String userId);
    
    /**
     * Get songs similar to a given song.
     */
    List<Song> getSimilarSongs(String songId, int limit);
    
    /**
     * Get available recommendation strategies.
     */
    List<String> getAvailableStrategies();
}



