package musicstreaming.models;

import musicstreaming.enums.Genre;
import musicstreaming.enums.SubscriptionType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * Represents a user of the music streaming service.
 */
public class User {
    private final String id;
    private String username;
    private String email;
    private String passwordHash;
    private String displayName;
    private String avatarUrl;
    private SubscriptionType subscriptionType;
    private final Set<Genre> preferredGenres;
    private final List<String> playlistIds;
    private final List<String> followedArtistIds;
    private final List<String> followedUserIds;
    private final List<ListeningHistoryEntry> listeningHistory;
    private boolean active;
    private final LocalDateTime createdAt;
    private LocalDateTime lastLoginAt;
    private LocalDateTime updatedAt;

    public User(String id, String username, String email, String passwordHash) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.passwordHash = passwordHash;
        this.displayName = username;
        this.subscriptionType = SubscriptionType.FREE;
        this.preferredGenres = new CopyOnWriteArraySet<>();
        this.playlistIds = new CopyOnWriteArrayList<>();
        this.followedArtistIds = new CopyOnWriteArrayList<>();
        this.followedUserIds = new CopyOnWriteArrayList<>();
        this.listeningHistory = new CopyOnWriteArrayList<>();
        this.active = true;
        this.createdAt = LocalDateTime.now();
        this.lastLoginAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getUsername() { return username; }
    public String getEmail() { return email; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public String getAvatarUrl() { return avatarUrl; }
    public SubscriptionType getSubscriptionType() { return subscriptionType; }
    public Set<Genre> getPreferredGenres() { return Collections.unmodifiableSet(preferredGenres); }
    public List<String> getPlaylistIds() { return Collections.unmodifiableList(playlistIds); }
    public List<String> getFollowedArtistIds() { return Collections.unmodifiableList(followedArtistIds); }
    public List<String> getFollowedUserIds() { return Collections.unmodifiableList(followedUserIds); }
    public List<ListeningHistoryEntry> getListeningHistory() { return Collections.unmodifiableList(listeningHistory); }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getLastLoginAt() { return lastLoginAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setUsername(String username) {
        this.username = username;
        this.updatedAt = LocalDateTime.now();
    }

    public void setEmail(String email) {
        this.email = email;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAvatarUrl(String avatarUrl) {
        this.avatarUrl = avatarUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setSubscriptionType(SubscriptionType subscriptionType) {
        this.subscriptionType = subscriptionType;
        this.updatedAt = LocalDateTime.now();
    }

    public void setActive(boolean active) {
        this.active = active;
        this.updatedAt = LocalDateTime.now();
    }

    public void updateLastLogin() {
        this.lastLoginAt = LocalDateTime.now();
    }

    // Preference management
    public void addPreferredGenre(Genre genre) {
        preferredGenres.add(genre);
        this.updatedAt = LocalDateTime.now();
    }

    public void removePreferredGenre(Genre genre) {
        preferredGenres.remove(genre);
        this.updatedAt = LocalDateTime.now();
    }

    // Playlist management
    public void addPlaylist(String playlistId) {
        if (!playlistIds.contains(playlistId)) {
            playlistIds.add(playlistId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removePlaylist(String playlistId) {
        playlistIds.remove(playlistId);
        this.updatedAt = LocalDateTime.now();
    }

    // Following management
    public void followArtist(String artistId) {
        if (!followedArtistIds.contains(artistId)) {
            followedArtistIds.add(artistId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void unfollowArtist(String artistId) {
        followedArtistIds.remove(artistId);
        this.updatedAt = LocalDateTime.now();
    }

    public void followUser(String userId) {
        if (!followedUserIds.contains(userId)) {
            followedUserIds.add(userId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void unfollowUser(String userId) {
        followedUserIds.remove(userId);
        this.updatedAt = LocalDateTime.now();
    }

    // Listening history
    public void addToListeningHistory(String songId) {
        listeningHistory.add(new ListeningHistoryEntry(songId, LocalDateTime.now()));
        // Keep only last 1000 entries
        while (listeningHistory.size() > 1000) {
            listeningHistory.remove(0);
        }
        this.updatedAt = LocalDateTime.now();
    }

    public boolean isPremium() {
        return subscriptionType == SubscriptionType.PREMIUM ||
               subscriptionType == SubscriptionType.FAMILY ||
               subscriptionType == SubscriptionType.STUDENT;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(id, user.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("User{id='%s', username='%s', subscription=%s}",
                id, username, subscriptionType);
    }

    /**
     * Represents a single entry in the listening history.
     */
    public static class ListeningHistoryEntry {
        private final String songId;
        private final LocalDateTime playedAt;

        public ListeningHistoryEntry(String songId, LocalDateTime playedAt) {
            this.songId = songId;
            this.playedAt = playedAt;
        }

        public String getSongId() { return songId; }
        public LocalDateTime getPlayedAt() { return playedAt; }
    }
}



