package musicstreaming.strategies.recommendation;

import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.SongRepository;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommends songs from artists the user follows or has listened to.
 */
public class ArtistBasedRecommendationStrategy implements RecommendationStrategy {

    private final SongRepository songRepository;

    public ArtistBasedRecommendationStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> recommend(User user, int limit) {
        List<String> followedArtists = user.getFollowedArtistIds();
        
        if (followedArtists.isEmpty()) {
            // Fallback to popular songs
            return songRepository.findTopByPlayCount(limit);
        }

        // Get songs user hasn't listened to recently
        Set<String> recentlyPlayed = user.getListeningHistory().stream()
                .map(User.ListeningHistoryEntry::getSongId)
                .collect(Collectors.toSet());

        List<Song> recommendations = new ArrayList<>();
        for (String artistId : followedArtists) {
            List<Song> artistSongs = songRepository.findByArtistId(artistId).stream()
                    .filter(song -> !recentlyPlayed.contains(song.getId()))
                    .collect(Collectors.toList());
            recommendations.addAll(artistSongs);
        }

        // Shuffle and limit
        Collections.shuffle(recommendations);
        return recommendations.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "ARTIST_BASED";
    }

    @Override
    public String getDescription() {
        return "Recommends songs from artists the user follows";
    }
}



