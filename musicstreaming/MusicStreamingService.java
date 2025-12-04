package musicstreaming;

import musicstreaming.enums.Genre;
import musicstreaming.enums.SubscriptionType;
import musicstreaming.models.*;
import musicstreaming.observers.*;
import musicstreaming.repositories.*;
import musicstreaming.repositories.impl.*;
import musicstreaming.services.*;
import musicstreaming.services.impl.*;
import musicstreaming.strategies.recommendation.*;
import musicstreaming.strategies.search.*;

import java.time.LocalDate;
import java.util.List;

/**
 * Main facade for the Music Streaming Service.
 * Provides a unified interface to all music streaming features.
 * 
 * Design Patterns Used:
 * - Facade: Simplifies interaction with the complex subsystem
 * - Singleton: Single instance of the service (optional)
 * - Factory Method: Creates instances of services
 * - Strategy: Pluggable search and recommendation algorithms
 * - Observer: Playback and playlist events
 * - Builder: Complex object creation (Playlist, Album)
 * - Repository: Data access abstraction
 */
public class MusicStreamingService {

    private static MusicStreamingService instance;

    // Repositories
    private final UserRepository userRepository;
    private final SongRepository songRepository;
    private final ArtistRepository artistRepository;
    private final AlbumRepository albumRepository;
    private final PlaylistRepository playlistRepository;

    // Services
    private final AuthenticationService authService;
    private final SongService songService;
    private final ArtistService artistService;
    private final AlbumService albumService;
    private final PlaylistServiceImpl playlistService;
    private final PlaybackServiceImpl playbackService;
    private final SearchService searchService;
    private final RecommendationServiceImpl recommendationService;

    /**
     * Private constructor for Singleton pattern.
     * Initializes all repositories, services, observers, and strategies.
     */
    private MusicStreamingService() {
        // Initialize repositories (DIP - Depend on abstractions)
        this.userRepository = new InMemoryUserRepository();
        this.songRepository = new InMemorySongRepository();
        this.artistRepository = new InMemoryArtistRepository();
        this.albumRepository = new InMemoryAlbumRepository();
        this.playlistRepository = new InMemoryPlaylistRepository();

        // Initialize services with dependency injection
        this.authService = new AuthenticationServiceImpl(userRepository);
        this.songService = new SongServiceImpl(songRepository);
        this.artistService = new ArtistServiceImpl(artistRepository);
        this.albumService = new AlbumServiceImpl(albumRepository, artistRepository, songRepository);
        this.playlistService = new PlaylistServiceImpl(playlistRepository, userRepository, songRepository);
        this.playbackService = new PlaybackServiceImpl(userRepository, songRepository, playlistRepository, albumRepository);

        // Initialize search strategies (Strategy Pattern)
        SearchStrategy<Song> songSearch = new SongTitleSearchStrategy(songRepository);
        SearchStrategy<Artist> artistSearch = new ArtistNameSearchStrategy(artistRepository);
        SearchStrategy<Album> albumSearch = new AlbumTitleSearchStrategy(albumRepository);
        SearchStrategy<Playlist> playlistSearch = new PlaylistNameSearchStrategy(playlistRepository);
        this.searchService = new SearchServiceImpl(songSearch, artistSearch, albumSearch, playlistSearch);

        // Initialize recommendation service with multiple strategies
        this.recommendationService = new RecommendationServiceImpl(userRepository, songRepository, playlistRepository);
        recommendationService.registerStrategy(new GenreBasedRecommendationStrategy(songRepository));
        recommendationService.registerStrategy(new TrendingRecommendationStrategy(songRepository));
        recommendationService.registerStrategy(new ArtistBasedRecommendationStrategy(songRepository));
        recommendationService.registerStrategy(new CollaborativeFilteringStrategy(songRepository, userRepository));

        // Register observers (Observer Pattern)
        playbackService.addObserver(new ListeningHistoryObserver());
        playbackService.addObserver(new ArtistStatsObserver(artistRepository));
    }

    /**
     * Get the singleton instance.
     */
    public static synchronized MusicStreamingService getInstance() {
        if (instance == null) {
            instance = new MusicStreamingService();
        }
        return instance;
    }

    /**
     * Reset the instance (for testing purposes).
     */
    public static synchronized void resetInstance() {
        instance = null;
    }

    // ==================== Authentication ====================

    public User register(String username, String email, String password) {
        return authService.register(username, email, password);
    }

    public String login(String usernameOrEmail, String password) {
        return authService.login(usernameOrEmail, password);
    }

    public User validateToken(String token) {
        return authService.validateToken(token);
    }

    public void logout(String token) {
        authService.logout(token);
    }

    // ==================== User Management ====================

    public User getUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    public void updateSubscription(String userId, SubscriptionType type) {
        userRepository.findById(userId).ifPresent(user -> {
            user.setSubscriptionType(type);
            userRepository.save(user);
        });
    }

    public void addPreferredGenre(String userId, Genre genre) {
        userRepository.findById(userId).ifPresent(user -> {
            user.addPreferredGenre(genre);
            userRepository.save(user);
        });
    }

    public void followArtist(String userId, String artistId) {
        userRepository.findById(userId).ifPresent(user -> {
            user.followArtist(artistId);
            userRepository.save(user);
        });
    }

    // ==================== Artist Management ====================

    public Artist createArtist(String name, String bio) {
        return artistService.createArtist(name, bio);
    }

    public Artist getArtist(String artistId) {
        return artistService.getArtist(artistId);
    }

    public List<Artist> getTopArtists(int limit) {
        return artistService.getTopArtists(limit);
    }

    public void addGenreToArtist(String artistId, Genre genre) {
        artistService.addGenreToArtist(artistId, genre);
    }

    // ==================== Song Management ====================

    public Song createSong(String title, String artistId, int durationSeconds, Genre genre) {
        return songService.createSong(title, artistId, durationSeconds, genre);
    }

    public Song getSong(String songId) {
        return songService.getSong(songId);
    }

    public List<Song> getTopSongs(int limit) {
        return songService.getTopSongs(limit);
    }

    public List<Song> getSongsByGenre(Genre genre) {
        return songService.getSongsByGenre(genre);
    }

    // ==================== Album Management ====================

    public Album createAlbum(String title, String artistId, Genre genre, LocalDate releaseDate) {
        return albumService.createAlbum(title, artistId, genre, releaseDate);
    }

    public Album getAlbum(String albumId) {
        return albumService.getAlbum(albumId);
    }

    public void addSongToAlbum(String albumId, String songId) {
        albumService.addSongToAlbum(albumId, songId);
    }

    // ==================== Playlist Management ====================

    public Playlist createPlaylist(String name, String ownerId, String description, boolean isPublic) {
        return playlistService.createPlaylist(name, ownerId, description, isPublic);
    }

    public Playlist getPlaylist(String playlistId) {
        return playlistService.getPlaylist(playlistId);
    }

    public List<Playlist> getUserPlaylists(String userId) {
        return playlistService.getUserPlaylists(userId);
    }

    public void addSongToPlaylist(String playlistId, String songId, String userId) {
        playlistService.addSongToPlaylist(playlistId, songId, userId);
    }

    public void removeSongFromPlaylist(String playlistId, String songId) {
        playlistService.removeSongFromPlaylist(playlistId, songId);
    }

    // ==================== Playback Control ====================

    public PlaybackSession playSong(String userId, String songId) {
        return playbackService.play(userId, songId);
    }

    public PlaybackSession playPlaylist(String userId, String playlistId) {
        return playbackService.playPlaylist(userId, playlistId, 0);
    }

    public PlaybackSession playAlbum(String userId, String albumId) {
        return playbackService.playAlbum(userId, albumId);
    }

    public void pause(String userId) {
        playbackService.pause(userId);
    }

    public void resume(String userId) {
        playbackService.resume(userId);
    }

    public String skipNext(String userId) {
        return playbackService.skipNext(userId);
    }

    public String skipPrevious(String userId) {
        return playbackService.skipPrevious(userId);
    }

    public void seek(String userId, int positionSeconds) {
        playbackService.seek(userId, positionSeconds);
    }

    public void toggleShuffle(String userId) {
        playbackService.toggleShuffle(userId);
    }

    public void cycleRepeatMode(String userId) {
        playbackService.cycleRepeatMode(userId);
    }

    public PlaybackSession getPlaybackSession(String userId) {
        return playbackService.getSession(userId);
    }

    // ==================== Search ====================

    public List<Song> searchSongs(String query, int limit) {
        return searchService.searchSongs(query, limit);
    }

    public List<Artist> searchArtists(String query, int limit) {
        return searchService.searchArtists(query, limit);
    }

    public List<Album> searchAlbums(String query, int limit) {
        return searchService.searchAlbums(query, limit);
    }

    public SearchService.SearchResults searchAll(String query, int limitPerType) {
        return searchService.searchAll(query, limitPerType);
    }

    // ==================== Recommendations ====================

    public List<Song> getRecommendations(String userId, int limit) {
        return recommendationService.getRecommendedSongs(userId, limit);
    }

    public List<Song> getRecommendations(String userId, String strategy, int limit) {
        return recommendationService.getRecommendedSongs(userId, strategy, limit);
    }

    public Playlist generateDiscoverWeekly(String userId) {
        return recommendationService.generateDiscoverWeekly(userId);
    }

    public List<Song> getSimilarSongs(String songId, int limit) {
        return recommendationService.getSimilarSongs(songId, limit);
    }

    public List<String> getAvailableRecommendationStrategies() {
        return recommendationService.getAvailableStrategies();
    }

    // ==================== Getters for testing ====================

    public UserRepository getUserRepository() { return userRepository; }
    public SongRepository getSongRepository() { return songRepository; }
    public ArtistRepository getArtistRepository() { return artistRepository; }
    public AlbumRepository getAlbumRepository() { return albumRepository; }
    public PlaylistRepository getPlaylistRepository() { return playlistRepository; }
}



