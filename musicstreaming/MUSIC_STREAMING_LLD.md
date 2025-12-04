# 🎵 Music Streaming Service - Low Level Design

## Overview

A comprehensive Low Level Design for an online music streaming service similar to Spotify, implementing clean architecture, SOLID principles, and multiple design patterns.

---

## Table of Contents

1. [Requirements](#requirements)
2. [Architecture Overview](#architecture-overview)
3. [Key Abstractions](#key-abstractions)
4. [Design Patterns](#design-patterns)
5. [SOLID Principles](#solid-principles)
6. [Class Diagrams](#class-diagrams)
7. [Component Details](#component-details)
8. [Extension Points](#extension-points)
9. [Concurrency Considerations](#concurrency-considerations)
10. [Usage Example](#usage-example)

---

## Requirements

### Functional Requirements

| Requirement | Implementation |
|-------------|----------------|
| Browse and search for songs, albums, and artists | `SearchService` with pluggable strategies |
| Create and manage playlists | `PlaylistService` with Observer pattern |
| User authentication and authorization | `AuthenticationService` with token-based auth |
| Play, pause, skip, and seek within songs | `PlaybackService` with State pattern |
| Recommend songs based on preferences | `RecommendationService` with Strategy pattern |
| Handle concurrent requests | Thread-safe collections, ConcurrentHashMap |
| Scalable for large volume | Repository abstraction, loose coupling |
| Extensible for new features | Strategy, Observer, Factory patterns |

---

## Architecture Overview

```
┌─────────────────────────────────────────────────────────────────────────┐
│                        MusicStreamingService                             │
│                         (Facade Pattern)                                 │
└─────────────────────────────────────────────────────────────────────────┘
                                    │
        ┌───────────────────────────┼───────────────────────────┐
        │                           │                           │
        ▼                           ▼                           ▼
┌───────────────┐         ┌───────────────┐         ┌───────────────┐
│Authentication │         │   Playback    │         │    Search     │
│   Service     │         │   Service     │         │   Service     │
└───────────────┘         └───────────────┘         └───────────────┘
                                    │                       │
                          ┌─────────┴─────────┐    ┌────────┴────────┐
                          │                   │    │                 │
                    ┌─────▼─────┐       ┌─────▼────▼─┐         ┌─────▼────┐
                    │ Observers │       │ Strategies │         │Strategies│
                    └───────────┘       └────────────┘         └──────────┘

┌─────────────────────────────────────────────────────────────────────────┐
│                        Repository Layer                                  │
│  UserRepo │ SongRepo │ ArtistRepo │ AlbumRepo │ PlaylistRepo            │
└─────────────────────────────────────────────────────────────────────────┘
```

---

## Key Abstractions

### Models

| Model | Purpose |
|-------|---------|
| `User` | User profile with preferences, listening history, subscriptions |
| `Song` | Audio track with metadata (title, duration, genre, play count) |
| `Artist` | Music creator with discography and monthly listeners |
| `Album` | Collection of songs by an artist (Builder pattern) |
| `Playlist` | User-created or system-generated song collection (Builder pattern) |
| `PlaybackSession` | Active playback state (current song, queue, shuffle, repeat) |

### Enums

| Enum | Values |
|------|--------|
| `Genre` | POP, ROCK, HIP_HOP, ELECTRONIC, JAZZ, CLASSICAL, etc. |
| `PlaybackState` | IDLE, PLAYING, PAUSED, BUFFERING, STOPPED |
| `SubscriptionType` | FREE, PREMIUM, FAMILY, STUDENT |
| `PlaylistType` | USER_CREATED, SYSTEM_GENERATED, ARTIST_CURATED, EDITORIAL |
| `SearchType` | SONG, ALBUM, ARTIST, PLAYLIST, ALL |

### Services

| Service | Responsibility |
|---------|----------------|
| `AuthenticationService` | User registration, login, token management |
| `SongService` | Song CRUD operations |
| `ArtistService` | Artist profile management |
| `AlbumService` | Album management with song associations |
| `PlaylistService` | Playlist CRUD with observer notifications |
| `PlaybackService` | Play, pause, skip, seek, queue management |
| `SearchService` | Multi-criteria search with pluggable strategies |
| `RecommendationService` | Generate recommendations using various strategies |

---

## Design Patterns

### 1. Facade Pattern
**Where:** `MusicStreamingService`

```java
public class MusicStreamingService {
    // Coordinates all services through a unified interface
    public User register(String username, String email, String password);
    public PlaybackSession playSong(String userId, String songId);
    public List<Song> searchSongs(String query, int limit);
    public List<Song> getRecommendations(String userId, int limit);
}
```

### 2. Strategy Pattern
**Where:** Search and Recommendation

```java
// Search strategies
public interface SearchStrategy<T> {
    List<T> search(String query, int limit);
}

// Implementations
- SongTitleSearchStrategy
- ArtistNameSearchStrategy  
- AlbumTitleSearchStrategy
- PlaylistNameSearchStrategy
- GenreSearchStrategy

// Recommendation strategies
public interface RecommendationStrategy {
    List<Song> recommend(User user, int limit);
}

// Implementations
- GenreBasedRecommendationStrategy
- TrendingRecommendationStrategy
- ArtistBasedRecommendationStrategy
- CollaborativeFilteringStrategy
```

### 3. Observer Pattern
**Where:** Playback and Playlist events

```java
public interface PlaybackObserver {
    void onSongStarted(User user, Song song);
    void onSongCompleted(User user, Song song);
    void onSongPaused(User user, Song song, int positionSeconds);
    void onSongSkipped(User user, Song song, int positionSeconds);
}

// Observers
- ListeningHistoryObserver (tracks user listening history)
- ArtistStatsObserver (updates artist monthly listeners)
```

### 4. Builder Pattern
**Where:** Complex object creation

```java
// Album Builder
Album album = new Album.Builder("id", "Title", "artistId")
    .genre(Genre.ROCK)
    .releaseDate(LocalDate.now())
    .addSong("song1")
    .build();

// Playlist Builder
Playlist playlist = new Playlist.Builder("id", "Name", "ownerId")
    .description("My favorite songs")
    .isPublic(true)
    .collaborative(true)
    .build();
```

### 5. Repository Pattern
**Where:** Data access layer

```java
public interface SongRepository {
    Song save(Song song);
    Optional<Song> findById(String id);
    List<Song> findByGenre(Genre genre);
    List<Song> findTopByPlayCount(int limit);
}

// In-memory implementation (can be swapped for DB)
public class InMemorySongRepository implements SongRepository { }
```

### 6. Singleton Pattern
**Where:** Service instance

```java
public static synchronized MusicStreamingService getInstance() {
    if (instance == null) {
        instance = new MusicStreamingService();
    }
    return instance;
}
```

---

## SOLID Principles

### Single Responsibility Principle (SRP)
Each service has one clear responsibility:
- `AuthenticationService` → Only authentication
- `PlaybackService` → Only playback control
- `SearchService` → Only search operations

### Open/Closed Principle (OCP)
- New search algorithms can be added without modifying `SearchService`
- New recommendation strategies can be registered at runtime
- New observers can be added without changing `PlaybackService`

### Liskov Substitution Principle (LSP)
All strategy implementations are interchangeable:
```java
SearchStrategy<Song> strategy = new SongTitleSearchStrategy(repo);
// Can be replaced with any SearchStrategy<Song>
```

### Interface Segregation Principle (ISP)
- `PlaybackObserver` has focused methods for specific events
- `SearchStrategy<T>` is generic and minimal
- Repository interfaces have focused query methods

### Dependency Inversion Principle (DIP)
- Services depend on repository interfaces, not implementations
- `SearchService` depends on `SearchStrategy<T>` interface
- Constructor injection used throughout

```java
public PlaybackServiceImpl(
    UserRepository userRepository,      // Interface
    SongRepository songRepository,      // Interface
    PlaylistRepository playlistRepository,
    AlbumRepository albumRepository
) { }
```

---

## Class Diagrams

### Core Models

```
┌──────────────────────┐
│        User          │
├──────────────────────┤
│ - id: String         │
│ - username: String   │
│ - email: String      │
│ - subscriptionType   │
│ - preferredGenres    │
│ - playlistIds        │
│ - listeningHistory   │
├──────────────────────┤
│ + addPreferredGenre()│
│ + followArtist()     │
│ + addToHistory()     │
└──────────────────────┘

┌──────────────────────┐
│        Song          │
├──────────────────────┤
│ - id: String         │
│ - title: String      │
│ - artistId: String   │
│ - durationSeconds    │
│ - genre: Genre       │
│ - playCount: int     │
├──────────────────────┤
│ + incrementPlayCount()│
│ + getFormattedDuration()│
└──────────────────────┘

┌──────────────────────────┐
│    PlaybackSession       │
├──────────────────────────┤
│ - currentSongId: String  │
│ - state: PlaybackState   │
│ - queue: List<String>    │
│ - shuffleEnabled: boolean│
│ - repeatMode: RepeatMode │
├──────────────────────────┤
│ + play(songId)           │
│ + pause()                │
│ + skipToNext()           │
│ + skipToPrevious()       │
│ + toggleShuffle()        │
└──────────────────────────┘
```

### Strategy Pattern

```
┌─────────────────────────────┐
│  <<interface>>              │
│  RecommendationStrategy     │
├─────────────────────────────┤
│ + recommend(user, limit)    │
│ + getStrategyName()         │
└─────────────────────────────┘
            △
            │
    ┌───────┴───────┐───────────────┐───────────────┐
    │               │               │               │
┌───▼───┐     ┌─────▼─────┐   ┌─────▼─────┐   ┌─────▼─────┐
│Genre  │     │ Trending  │   │  Artist   │   │Collaborative│
│Based  │     │           │   │  Based    │   │ Filtering   │
└───────┘     └───────────┘   └───────────┘   └─────────────┘
```

---

## Component Details

### PlaybackService Flow

```
User Action          Service Method           Observer Notifications
──────────────────────────────────────────────────────────────────────
Play Song      →    play(userId, songId)    →  onSongStarted()
                                                ↓
Pause          →    pause(userId)           →  onSongPaused()
                                                ↓
Resume         →    resume(userId)          →  onSongStarted()
                                                ↓
Skip Next      →    skipNext(userId)        →  onSongSkipped()
                                               onSongStarted()
```

### Recommendation Flow

```
                    ┌──────────────────────────┐
                    │  RecommendationService   │
                    └──────────────────────────┘
                              │
         ┌────────────────────┼────────────────────┐
         ▼                    ▼                    ▼
┌─────────────────┐  ┌─────────────────┐  ┌─────────────────┐
│  Get User       │  │  Select Strategy│  │  Apply Strategy │
│  Preferences    │  │  (or default)   │  │  recommend()    │
└─────────────────┘  └─────────────────┘  └─────────────────┘
         │                    │                    │
         └────────────────────┼────────────────────┘
                              ▼
                    ┌──────────────────────────┐
                    │  Return Recommendations  │
                    └──────────────────────────┘
```

---

## Extension Points

### Adding a New Search Strategy

```java
// 1. Create new strategy
public class LyricsSearchStrategy implements SearchStrategy<Song> {
    @Override
    public List<Song> search(String query, int limit) {
        // Search by lyrics
    }
}

// 2. Inject into SearchService (OCP - no modification needed)
```

### Adding a New Recommendation Strategy

```java
// 1. Implement the strategy
public class MoodBasedStrategy implements RecommendationStrategy {
    @Override
    public List<Song> recommend(User user, int limit) {
        // Recommend based on time of day/mood
    }
}

// 2. Register with service
recommendationService.registerStrategy(new MoodBasedStrategy());
```

### Adding a New Observer

```java
// 1. Implement observer
public class SocialSharingObserver implements PlaybackObserver {
    @Override
    public void onSongCompleted(User user, Song song) {
        // Share on social media
    }
}

// 2. Register
playbackService.addObserver(new SocialSharingObserver());
```

### Supporting Offline Playback

```java
// Add new service
public interface OfflineService {
    void downloadSong(String userId, String songId);
    void downloadPlaylist(String userId, String playlistId);
    List<Song> getOfflineSongs(String userId);
}
```

---

## Concurrency Considerations

### Thread-Safe Collections

```java
// Used throughout for concurrent access
private final Map<String, User> users = new ConcurrentHashMap<>();
private final List<PlaybackObserver> observers = new CopyOnWriteArrayList<>();
```

### Atomic Operations

```java
// Session management is atomic
return activeSessions.computeIfAbsent(userId, 
    id -> new PlaybackSession(UUID.randomUUID().toString(), id));
```

### Synchronized Access

```java
public static synchronized MusicStreamingService getInstance() {
    if (instance == null) {
        instance = new MusicStreamingService();
    }
    return instance;
}
```

---

## Usage Example

```java
// Get service instance
MusicStreamingService service = MusicStreamingService.getInstance();

// Register and login
User user = service.register("john", "john@email.com", "password");
String token = service.login("john", "password");

// Create content
Artist artist = service.createArtist("The Band", "Rock band from NYC");
Song song = service.createSong("Hit Song", artist.getId(), 240, Genre.ROCK);

// Create playlist
Playlist playlist = service.createPlaylist("My Mix", user.getId(), "Favorites", true);
service.addSongToPlaylist(playlist.getId(), song.getId(), user.getId());

// Playback
service.playSong(user.getId(), song.getId());
service.toggleShuffle(user.getId());
service.skipNext(user.getId());

// Search
List<Song> results = service.searchSongs("Hit", 10);

// Recommendations
List<Song> recommendations = service.getRecommendations(user.getId(), 10);
```

---

## File Structure

```
musicstreaming/
├── enums/
│   ├── Genre.java
│   ├── PlaybackState.java
│   ├── PlaylistType.java
│   ├── SearchType.java
│   └── SubscriptionType.java
├── exceptions/
│   ├── AuthenticationException.java
│   ├── MusicStreamingException.java
│   ├── PlaybackException.java
│   ├── PlaylistNotFoundException.java
│   ├── SongNotFoundException.java
│   ├── UnauthorizedException.java
│   └── UserNotFoundException.java
├── models/
│   ├── Album.java
│   ├── Artist.java
│   ├── PlaybackSession.java
│   ├── Playlist.java
│   ├── Song.java
│   └── User.java
├── observers/
│   ├── ArtistStatsObserver.java
│   ├── ListeningHistoryObserver.java
│   ├── PlaybackObserver.java
│   └── PlaylistObserver.java
├── repositories/
│   ├── impl/
│   │   ├── InMemoryAlbumRepository.java
│   │   ├── InMemoryArtistRepository.java
│   │   ├── InMemoryPlaylistRepository.java
│   │   ├── InMemorySongRepository.java
│   │   └── InMemoryUserRepository.java
│   ├── AlbumRepository.java
│   ├── ArtistRepository.java
│   ├── PlaylistRepository.java
│   ├── SongRepository.java
│   └── UserRepository.java
├── services/
│   ├── impl/
│   │   ├── AlbumServiceImpl.java
│   │   ├── ArtistServiceImpl.java
│   │   ├── AuthenticationServiceImpl.java
│   │   ├── PlaybackServiceImpl.java
│   │   ├── PlaylistServiceImpl.java
│   │   ├── RecommendationServiceImpl.java
│   │   ├── SearchServiceImpl.java
│   │   └── SongServiceImpl.java
│   ├── AlbumService.java
│   ├── ArtistService.java
│   ├── AuthenticationService.java
│   ├── PlaybackService.java
│   ├── PlaylistService.java
│   ├── RecommendationService.java
│   ├── SearchService.java
│   └── SongService.java
├── strategies/
│   ├── recommendation/
│   │   ├── ArtistBasedRecommendationStrategy.java
│   │   ├── CollaborativeFilteringStrategy.java
│   │   ├── GenreBasedRecommendationStrategy.java
│   │   ├── RecommendationStrategy.java
│   │   └── TrendingRecommendationStrategy.java
│   └── search/
│       ├── AlbumTitleSearchStrategy.java
│       ├── ArtistNameSearchStrategy.java
│       ├── GenreSearchStrategy.java
│       ├── PlaylistNameSearchStrategy.java
│       ├── SearchStrategy.java
│       └── SongTitleSearchStrategy.java
├── Main.java
└── MusicStreamingService.java
```

---

## Design Rationale

### Why This Design is Extensible

1. **Strategy Pattern** allows adding new search/recommendation algorithms without changing existing code
2. **Observer Pattern** enables adding new event handlers (analytics, social sharing) easily
3. **Repository Pattern** allows swapping in-memory storage for database without service changes
4. **Interface Segregation** keeps contracts minimal and focused

### Why This Design is Loosely Coupled

1. **Dependency Injection** - All dependencies passed via constructors
2. **Programming to Interfaces** - Services depend on abstractions
3. **Facade Pattern** - Clients interact with unified interface, not internal services
4. **Event-driven Communication** - Components communicate via observers

### Why This Design is SOLID-Compliant

1. **SRP** - Each class has a single, well-defined responsibility
2. **OCP** - Strategies can be added without modifying existing code
3. **LSP** - All implementations are substitutable for their interfaces
4. **ISP** - Interfaces are small and focused
5. **DIP** - High-level modules don't depend on low-level implementations

---

## Future Enhancements

1. **Offline Mode** - Download songs for offline playback
2. **Social Features** - Share playlists, follow friends
3. **Lyrics Display** - Show synchronized lyrics
4. **Podcast Support** - Extend to support podcasts
5. **Cross-device Sync** - Sync playback across devices
6. **Audio Quality Settings** - Allow users to choose quality
7. **Gapless Playback** - Seamless transitions between songs



