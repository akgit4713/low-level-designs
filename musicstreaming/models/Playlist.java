package musicstreaming.models;

import musicstreaming.enums.PlaylistType;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a playlist containing an ordered list of songs.
 */
public class Playlist {
    private final String id;
    private String name;
    private String description;
    private final String ownerId;
    private String coverImageUrl;
    private PlaylistType type;
    private final List<PlaylistTrack> tracks;
    private boolean isPublic;
    private boolean collaborative;
    private int followerCount;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Playlist(Builder builder) {
        this.id = builder.id;
        this.name = builder.name;
        this.description = builder.description;
        this.ownerId = builder.ownerId;
        this.coverImageUrl = builder.coverImageUrl;
        this.type = builder.type;
        this.tracks = new CopyOnWriteArrayList<>(builder.tracks);
        this.isPublic = builder.isPublic;
        this.collaborative = builder.collaborative;
        this.followerCount = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public String getOwnerId() { return ownerId; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public PlaylistType getType() { return type; }
    public List<PlaylistTrack> getTracks() { return Collections.unmodifiableList(tracks); }
    public boolean isPublic() { return isPublic; }
    public boolean isCollaborative() { return collaborative; }
    public int getFollowerCount() { return followerCount; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Get song IDs only
    public List<String> getSongIds() {
        List<String> songIds = new ArrayList<>();
        for (PlaylistTrack track : tracks) {
            songIds.add(track.getSongId());
        }
        return songIds;
    }

    // Setters
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDescription(String description) {
        this.description = description;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setPublic(boolean isPublic) {
        this.isPublic = isPublic;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCollaborative(boolean collaborative) {
        this.collaborative = collaborative;
        this.updatedAt = LocalDateTime.now();
    }

    // Track management
    public void addTrack(String songId, String addedBy) {
        tracks.add(new PlaylistTrack(songId, addedBy, LocalDateTime.now()));
        this.updatedAt = LocalDateTime.now();
    }

    public void addTrackAt(int index, String songId, String addedBy) {
        if (index >= 0 && index <= tracks.size()) {
            tracks.add(index, new PlaylistTrack(songId, addedBy, LocalDateTime.now()));
            this.updatedAt = LocalDateTime.now();
        }
    }

    public boolean removeTrack(String songId) {
        boolean removed = tracks.removeIf(track -> track.getSongId().equals(songId));
        if (removed) {
            this.updatedAt = LocalDateTime.now();
        }
        return removed;
    }

    public void reorderTrack(int fromIndex, int toIndex) {
        if (fromIndex >= 0 && fromIndex < tracks.size() && 
            toIndex >= 0 && toIndex < tracks.size()) {
            PlaylistTrack track = tracks.remove(fromIndex);
            tracks.add(toIndex, track);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void clearTracks() {
        tracks.clear();
        this.updatedAt = LocalDateTime.now();
    }

    public int getTrackCount() {
        return tracks.size();
    }

    public void incrementFollowerCount() {
        this.followerCount++;
    }

    public void decrementFollowerCount() {
        if (this.followerCount > 0) {
            this.followerCount--;
        }
    }

    public int getTotalDurationSeconds(Map<String, Song> songMap) {
        return tracks.stream()
                .map(track -> songMap.get(track.getSongId()))
                .filter(Objects::nonNull)
                .mapToInt(Song::getDurationSeconds)
                .sum();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Playlist playlist = (Playlist) o;
        return Objects.equals(id, playlist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Playlist{id='%s', name='%s', owner='%s', tracks=%d, public=%s}",
                id, name, ownerId, tracks.size(), isPublic);
    }

    /**
     * Represents a track entry in the playlist with metadata.
     */
    public static class PlaylistTrack {
        private final String songId;
        private final String addedBy;
        private final LocalDateTime addedAt;

        public PlaylistTrack(String songId, String addedBy, LocalDateTime addedAt) {
            this.songId = songId;
            this.addedBy = addedBy;
            this.addedAt = addedAt;
        }

        public String getSongId() { return songId; }
        public String getAddedBy() { return addedBy; }
        public LocalDateTime getAddedAt() { return addedAt; }
    }

    // Builder Pattern
    public static class Builder {
        private final String id;
        private final String name;
        private final String ownerId;
        private String description = "";
        private String coverImageUrl;
        private PlaylistType type = PlaylistType.USER_CREATED;
        private final List<PlaylistTrack> tracks = new ArrayList<>();
        private boolean isPublic = false;
        private boolean collaborative = false;

        public Builder(String id, String name, String ownerId) {
            this.id = id;
            this.name = name;
            this.ownerId = ownerId;
        }

        public Builder description(String description) {
            this.description = description;
            return this;
        }

        public Builder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public Builder type(PlaylistType type) {
            this.type = type;
            return this;
        }

        public Builder isPublic(boolean isPublic) {
            this.isPublic = isPublic;
            return this;
        }

        public Builder collaborative(boolean collaborative) {
            this.collaborative = collaborative;
            return this;
        }

        public Playlist build() {
            return new Playlist(this);
        }
    }
}



