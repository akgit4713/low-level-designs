package musicstreaming.services.impl;

import musicstreaming.enums.PlaylistType;
import musicstreaming.exceptions.UserNotFoundException;
import musicstreaming.models.Playlist;
import musicstreaming.models.Song;
import musicstreaming.models.User;
import musicstreaming.repositories.PlaylistRepository;
import musicstreaming.repositories.SongRepository;
import musicstreaming.repositories.UserRepository;
import musicstreaming.services.RecommendationService;
import musicstreaming.strategies.recommendation.RecommendationStrategy;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Implementation of RecommendationService with multiple strategies.
 */
public class RecommendationServiceImpl implements RecommendationService {

    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final PlaylistRepository playlistRepository;
    private final Map<String, RecommendationStrategy> strategies = new HashMap<>();
    private String defaultStrategy = "GENRE_BASED";

    public RecommendationServiceImpl(UserRepository userRepository,
                                    SongRepository songRepository,
                                    PlaylistRepository playlistRepository) {
        this.userRepository = userRepository;
        this.songRepository = songRepository;
        this.playlistRepository = playlistRepository;
    }

    /**
     * Register a recommendation strategy.
     */
    public void registerStrategy(RecommendationStrategy strategy) {
        strategies.put(strategy.getStrategyName(), strategy);
    }

    /**
     * Set the default strategy to use.
     */
    public void setDefaultStrategy(String strategyName) {
        if (!strategies.containsKey(strategyName)) {
            throw new IllegalArgumentException("Unknown strategy: " + strategyName);
        }
        this.defaultStrategy = strategyName;
    }

    @Override
    public List<Song> getRecommendedSongs(String userId, int limit) {
        return getRecommendedSongs(userId, defaultStrategy, limit);
    }

    @Override
    public List<Song> getRecommendedSongs(String userId, String strategyName, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        RecommendationStrategy strategy = strategies.get(strategyName);
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown recommendation strategy: " + strategyName);
        }

        return strategy.recommend(user, limit);
    }

    @Override
    public List<Playlist> getRecommendedPlaylists(String userId, int limit) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Get popular public playlists that match user's genres
        Set<musicstreaming.enums.Genre> userGenres = user.getPreferredGenres();
        
        return playlistRepository.findTopByFollowerCount(limit * 2).stream()
                .filter(Playlist::isPublic)
                .filter(playlist -> !playlist.getOwnerId().equals(userId))
                .filter(playlist -> !user.getPlaylistIds().contains(playlist.getId()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public Playlist generateDiscoverWeekly(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));

        // Get recommendations from multiple strategies and combine
        Set<Song> allRecommendations = new LinkedHashSet<>();
        
        for (RecommendationStrategy strategy : strategies.values()) {
            List<Song> recommendations = strategy.recommend(user, 10);
            allRecommendations.addAll(recommendations);
        }

        // Create a system-generated playlist
        String playlistId = "discover-weekly-" + userId + "-" + System.currentTimeMillis();
        Playlist.Builder builder = new Playlist.Builder(playlistId, "Discover Weekly", "system")
                .description("Made for " + user.getDisplayName() + ". Your personal mixtape of fresh music.")
                .type(PlaylistType.SYSTEM_GENERATED)
                .isPublic(false);

        Playlist playlist = builder.build();
        
        // Add recommended songs
        List<Song> finalList = new ArrayList<>(allRecommendations);
        Collections.shuffle(finalList);
        for (Song song : finalList.stream().limit(30).collect(Collectors.toList())) {
            playlist.addTrack(song.getId(), "system");
        }

        return playlistRepository.save(playlist);
    }

    @Override
    public List<Song> getSimilarSongs(String songId, int limit) {
        Song song = songRepository.findById(songId)
                .orElseThrow(() -> new IllegalArgumentException("Song not found: " + songId));

        // Find songs with same genre and artist
        List<Song> similar = new ArrayList<>();
        
        // Same artist
        similar.addAll(songRepository.findByArtistId(song.getArtistId()).stream()
                .filter(s -> !s.getId().equals(songId))
                .collect(Collectors.toList()));

        // Same genre
        if (song.getGenre() != null) {
            similar.addAll(songRepository.findByGenre(song.getGenre()).stream()
                    .filter(s -> !s.getId().equals(songId))
                    .filter(s -> !similar.contains(s))
                    .collect(Collectors.toList()));
        }

        // Shuffle and limit
        Collections.shuffle(similar);
        return similar.stream().limit(limit).collect(Collectors.toList());
    }

    @Override
    public List<String> getAvailableStrategies() {
        return new ArrayList<>(strategies.keySet());
    }
}



