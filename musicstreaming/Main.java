package musicstreaming;

import musicstreaming.enums.Genre;
import musicstreaming.enums.SubscriptionType;
import musicstreaming.models.*;
import musicstreaming.services.SearchService;

import java.time.LocalDate;
import java.util.List;

/**
 * Demo application showcasing the Music Streaming Service.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║          🎵 MUSIC STREAMING SERVICE DEMO 🎵                  ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝\n");

        MusicStreamingService service = MusicStreamingService.getInstance();

        // ==================== 1. User Registration & Authentication ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📝 USER REGISTRATION & AUTHENTICATION");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        User user1 = service.register("john_doe", "john@example.com", "password123");
        System.out.println("✓ Registered user: " + user1.getUsername());

        User user2 = service.register("jane_smith", "jane@example.com", "password456");
        System.out.println("✓ Registered user: " + user2.getUsername());

        String token1 = service.login("john_doe", "password123");
        System.out.println("✓ Logged in as john_doe, token: " + token1.substring(0, 8) + "...");

        // Update subscription
        service.updateSubscription(user1.getId(), SubscriptionType.PREMIUM);
        System.out.println("✓ Updated john_doe to PREMIUM subscription\n");

        // ==================== 2. Create Artists ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎤 CREATING ARTISTS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        Artist artist1 = service.createArtist("The Midnight", "Synthwave duo from Los Angeles");
        service.addGenreToArtist(artist1.getId(), Genre.ELECTRONIC);
        System.out.println("✓ Created artist: " + artist1.getName());

        Artist artist2 = service.createArtist("Arctic Monkeys", "English rock band from Sheffield");
        service.addGenreToArtist(artist2.getId(), Genre.ROCK);
        service.addGenreToArtist(artist2.getId(), Genre.INDIE);
        System.out.println("✓ Created artist: " + artist2.getName());

        Artist artist3 = service.createArtist("Kendrick Lamar", "American rapper from Compton");
        service.addGenreToArtist(artist3.getId(), Genre.HIP_HOP);
        System.out.println("✓ Created artist: " + artist3.getName() + "\n");

        // ==================== 3. Create Songs ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🎶 CREATING SONGS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // The Midnight songs
        Song song1 = service.createSong("Sunset", artist1.getId(), 245, Genre.ELECTRONIC);
        Song song2 = service.createSong("Days of Thunder", artist1.getId(), 312, Genre.ELECTRONIC);
        Song song3 = service.createSong("Los Angeles", artist1.getId(), 280, Genre.ELECTRONIC);

        // Arctic Monkeys songs
        Song song4 = service.createSong("Do I Wanna Know?", artist2.getId(), 272, Genre.ROCK);
        Song song5 = service.createSong("505", artist2.getId(), 253, Genre.INDIE);
        Song song6 = service.createSong("R U Mine?", artist2.getId(), 201, Genre.ROCK);

        // Kendrick Lamar songs
        Song song7 = service.createSong("HUMBLE.", artist3.getId(), 177, Genre.HIP_HOP);
        Song song8 = service.createSong("DNA.", artist3.getId(), 185, Genre.HIP_HOP);
        Song song9 = service.createSong("Alright", artist3.getId(), 219, Genre.HIP_HOP);

        System.out.println("✓ Created 9 songs across 3 artists\n");

        // ==================== 4. Create Albums ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("💿 CREATING ALBUMS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        Album album1 = service.createAlbum("Endless Summer", artist1.getId(), Genre.ELECTRONIC, LocalDate.of(2016, 7, 15));
        service.addSongToAlbum(album1.getId(), song1.getId());
        service.addSongToAlbum(album1.getId(), song2.getId());
        service.addSongToAlbum(album1.getId(), song3.getId());
        System.out.println("✓ Created album: " + album1.getTitle() + " with " + album1.getSongCount() + " songs");

        Album album2 = service.createAlbum("AM", artist2.getId(), Genre.ROCK, LocalDate.of(2013, 9, 9));
        service.addSongToAlbum(album2.getId(), song4.getId());
        service.addSongToAlbum(album2.getId(), song5.getId());
        service.addSongToAlbum(album2.getId(), song6.getId());
        System.out.println("✓ Created album: " + album2.getTitle() + " with " + album2.getSongCount() + " songs");

        Album album3 = service.createAlbum("DAMN.", artist3.getId(), Genre.HIP_HOP, LocalDate.of(2017, 4, 14));
        service.addSongToAlbum(album3.getId(), song7.getId());
        service.addSongToAlbum(album3.getId(), song8.getId());
        service.addSongToAlbum(album3.getId(), song9.getId());
        System.out.println("✓ Created album: " + album3.getTitle() + " with " + album3.getSongCount() + " songs\n");

        // ==================== 5. Create Playlists ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("📋 CREATING PLAYLISTS");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        Playlist playlist1 = service.createPlaylist("My Favorites", user1.getId(), "Best songs ever!", true);
        service.addSongToPlaylist(playlist1.getId(), song1.getId(), user1.getId());
        service.addSongToPlaylist(playlist1.getId(), song4.getId(), user1.getId());
        service.addSongToPlaylist(playlist1.getId(), song7.getId(), user1.getId());
        System.out.println("✓ Created playlist: " + playlist1.getName() + " with " + playlist1.getTrackCount() + " tracks");

        Playlist playlist2 = service.createPlaylist("Late Night Vibes", user1.getId(), "Chill electronic music", true);
        service.addSongToPlaylist(playlist2.getId(), song1.getId(), user1.getId());
        service.addSongToPlaylist(playlist2.getId(), song2.getId(), user1.getId());
        service.addSongToPlaylist(playlist2.getId(), song3.getId(), user1.getId());
        System.out.println("✓ Created playlist: " + playlist2.getName() + " with " + playlist2.getTrackCount() + " tracks\n");

        // ==================== 6. User Preferences ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("⚙️ SETTING USER PREFERENCES");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        service.addPreferredGenre(user1.getId(), Genre.ELECTRONIC);
        service.addPreferredGenre(user1.getId(), Genre.ROCK);
        service.followArtist(user1.getId(), artist1.getId());
        service.followArtist(user1.getId(), artist2.getId());
        System.out.println("✓ Set preferred genres: ELECTRONIC, ROCK");
        System.out.println("✓ Following artists: The Midnight, Arctic Monkeys\n");

        // ==================== 7. Playback Demo ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("▶️ PLAYBACK DEMO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Play a single song
        PlaybackSession session = service.playSong(user1.getId(), song1.getId());
        System.out.println("▶ Now playing: " + service.getSong(session.getCurrentSongId()).getTitle());
        System.out.println("  State: " + session.getState());

        // Pause
        service.pause(user1.getId());
        session = service.getPlaybackSession(user1.getId());
        System.out.println("⏸ Paused. State: " + session.getState());

        // Resume
        service.resume(user1.getId());
        session = service.getPlaybackSession(user1.getId());
        System.out.println("▶ Resumed. State: " + session.getState());

        // Play album
        System.out.println("\n🎵 Playing album: " + album2.getTitle());
        session = service.playAlbum(user1.getId(), album2.getId());
        System.out.println("▶ Now playing: " + service.getSong(session.getCurrentSongId()).getTitle());

        // Skip to next
        String nextSongId = service.skipNext(user1.getId());
        System.out.println("⏭ Skipped to: " + service.getSong(nextSongId).getTitle());

        // Toggle shuffle
        service.toggleShuffle(user1.getId());
        session = service.getPlaybackSession(user1.getId());
        System.out.println("🔀 Shuffle: " + (session.isShuffleEnabled() ? "ON" : "OFF"));

        // Cycle repeat mode
        service.cycleRepeatMode(user1.getId());
        session = service.getPlaybackSession(user1.getId());
        System.out.println("🔁 Repeat mode: " + session.getRepeatMode() + "\n");

        // ==================== 8. Search Demo ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔍 SEARCH DEMO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Search songs
        List<Song> songResults = service.searchSongs("Sun", 5);
        System.out.println("Search 'Sun' in songs: " + songResults.size() + " results");
        for (Song s : songResults) {
            System.out.println("  • " + s.getTitle() + " by " + service.getArtist(s.getArtistId()).getName());
        }

        // Search artists
        List<Artist> artistResults = service.searchArtists("Arctic", 5);
        System.out.println("\nSearch 'Arctic' in artists: " + artistResults.size() + " results");
        for (Artist a : artistResults) {
            System.out.println("  • " + a.getName());
        }

        // Unified search
        SearchService.SearchResults allResults = service.searchAll("a", 3);
        System.out.println("\nUnified search 'a': " + allResults.getTotalCount() + " total results");
        System.out.println("  Songs: " + allResults.getSongs().size());
        System.out.println("  Artists: " + allResults.getArtists().size());
        System.out.println("  Albums: " + allResults.getAlbums().size());
        System.out.println("  Playlists: " + allResults.getPlaylists().size() + "\n");

        // ==================== 9. Recommendations Demo ====================
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("✨ RECOMMENDATIONS DEMO");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        // Show available strategies
        System.out.println("Available recommendation strategies:");
        for (String strategy : service.getAvailableRecommendationStrategies()) {
            System.out.println("  • " + strategy);
        }

        // Genre-based recommendations
        System.out.println("\n📊 Genre-based recommendations for " + user1.getUsername() + ":");
        List<Song> genreRecs = service.getRecommendations(user1.getId(), "GENRE_BASED", 3);
        for (Song s : genreRecs) {
            System.out.println("  ♪ " + s.getTitle() + " (" + s.getGenre() + ")");
        }

        // Trending recommendations
        System.out.println("\n🔥 Trending recommendations:");
        List<Song> trendingRecs = service.getRecommendations(user1.getId(), "TRENDING", 3);
        for (Song s : trendingRecs) {
            System.out.println("  ♪ " + s.getTitle() + " - " + s.getPlayCount() + " plays");
        }

        // Similar songs
        System.out.println("\n🎯 Songs similar to '" + song1.getTitle() + "':");
        List<Song> similarSongs = service.getSimilarSongs(song1.getId(), 3);
        for (Song s : similarSongs) {
            System.out.println("  ♪ " + s.getTitle());
        }

        // Generate Discover Weekly
        System.out.println("\n📅 Generating Discover Weekly playlist...");
        Playlist discoverWeekly = service.generateDiscoverWeekly(user1.getId());
        System.out.println("  Created: " + discoverWeekly.getName());
        System.out.println("  Tracks: " + discoverWeekly.getTrackCount());

        // ==================== Summary ====================
        System.out.println("\n╔══════════════════════════════════════════════════════════════╗");
        System.out.println("║                    ✅ DEMO COMPLETED                         ║");
        System.out.println("╚══════════════════════════════════════════════════════════════╝");
        System.out.println("\nSummary:");
        System.out.println("  • Users registered: 2");
        System.out.println("  • Artists created: 3");
        System.out.println("  • Songs created: 9");
        System.out.println("  • Albums created: 3");
        System.out.println("  • Playlists created: 3 (including Discover Weekly)");
        System.out.println("\nDesign Patterns demonstrated:");
        System.out.println("  ✓ Facade Pattern - MusicStreamingService");
        System.out.println("  ✓ Strategy Pattern - Search & Recommendation strategies");
        System.out.println("  ✓ Observer Pattern - Playback & Playlist events");
        System.out.println("  ✓ Builder Pattern - Playlist & Album creation");
        System.out.println("  ✓ Repository Pattern - Data access abstraction");
        System.out.println("  ✓ Singleton Pattern - Service instance");
        System.out.println("  ✓ Dependency Injection - All services");
    }
}



