package musicstreaming.strategies.recommendation;

import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.SongRepository;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Recommends currently trending/popular songs.
 */
public class TrendingRecommendationStrategy implements RecommendationStrategy {

    private final SongRepository songRepository;

    public TrendingRecommendationStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> recommend(User user, int limit) {
        // Get songs user hasn't listened to recently
        Set<String> recentlyPlayed = user.getListeningHistory().stream()
                .map(User.ListeningHistoryEntry::getSongId)
                .collect(Collectors.toSet());

        return songRepository.findTopByPlayCount(limit * 2).stream()
                .filter(song -> !recentlyPlayed.contains(song.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "TRENDING";
    }

    @Override
    public String getDescription() {
        return "Recommends currently popular and trending songs";
    }
}



