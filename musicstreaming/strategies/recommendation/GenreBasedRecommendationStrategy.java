package musicstreaming.strategies.recommendation;

import musicstreaming.enums.Genre;
import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.SongRepository;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommends songs based on user's preferred genres.
 */
public class GenreBasedRecommendationStrategy implements RecommendationStrategy {

    private final SongRepository songRepository;

    public GenreBasedRecommendationStrategy(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    @Override
    public List<Song> recommend(User user, int limit) {
        Set<Genre> preferredGenres = user.getPreferredGenres();
        
        if (preferredGenres.isEmpty()) {
            // Fallback to popular songs if no preferences set
            return songRepository.findTopByPlayCount(limit);
        }

        // Get songs from all preferred genres
        Set<String> recentlyPlayed = user.getListeningHistory().stream()
                .map(User.ListeningHistoryEntry::getSongId)
                .collect(Collectors.toSet());

        List<Song> recommendations = new ArrayList<>();
        for (Genre genre : preferredGenres) {
            List<Song> genreSongs = songRepository.findByGenre(genre).stream()
                    .filter(song -> !recentlyPlayed.contains(song.getId()))
                    .collect(Collectors.toList());
            recommendations.addAll(genreSongs);
        }

        // Shuffle and limit
        Collections.shuffle(recommendations);
        return recommendations.stream()
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getStrategyName() {
        return "GENRE_BASED";
    }

    @Override
    public String getDescription() {
        return "Recommends songs based on user's preferred music genres";
    }
}



