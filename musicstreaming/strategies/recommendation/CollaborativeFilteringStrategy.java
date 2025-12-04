package musicstreaming.strategies.recommendation;

import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.SongRepository;
import musicstreaming.repositories.UserRepository;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Recommends songs based on what similar users are listening to.
 * Simplified collaborative filtering based on genre overlap.
 */
public class CollaborativeFilteringStrategy implements RecommendationStrategy {

    private final SongRepository songRepository;
    private final UserRepository userRepository;

    public CollaborativeFilteringStrategy(SongRepository songRepository, UserRepository userRepository) {
        this.songRepository = songRepository;
        this.userRepository = userRepository;
    }

    @Override
    public List<Song> recommend(User user, int limit) {
        // Find similar users based on genre preferences
        List<User> similarUsers = findSimilarUsers(user);
        
        if (similarUsers.isEmpty()) {
            return songRepository.findTopByPlayCount(limit);
        }

        // Get songs that similar users have listened to
        Set<String> userListenedSongs = user.getListeningHistory().stream()
                .map(User.ListeningHistoryEntry::getSongId)
                .collect(Collectors.toSet());

        Map<String, Integer> songScores = new HashMap<>();
        
        for (User similarUser : similarUsers) {
            for (User.ListeningHistoryEntry entry : similarUser.getListeningHistory()) {
                String songId = entry.getSongId();
                if (!userListenedSongs.contains(songId)) {
                    songScores.merge(songId, 1, Integer::sum);
                }
            }
        }

        // Sort by score and get top recommendations
        return songScores.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .map(songRepository::findById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    private List<User> findSimilarUsers(User targetUser) {
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(targetUser.getId()))
                .filter(u -> hasGenreOverlap(targetUser, u))
                .limit(10)
                .collect(Collectors.toList());
    }

    private boolean hasGenreOverlap(User user1, User user2) {
        Set<musicstreaming.enums.Genre> genres1 = user1.getPreferredGenres();
        Set<musicstreaming.enums.Genre> genres2 = user2.getPreferredGenres();
        
        for (musicstreaming.enums.Genre genre : genres1) {
            if (genres2.contains(genre)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getStrategyName() {
        return "COLLABORATIVE_FILTERING";
    }

    @Override
    public String getDescription() {
        return "Recommends songs based on what similar users are listening to";
    }
}



