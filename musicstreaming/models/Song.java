package musicstreaming.models;

import musicstreaming.enums.Genre;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Represents a song/track in the music library.
 */
public class Song {
    private final String id;
    private String title;
    private final String artistId;
    private String albumId;
    private int durationSeconds;
    private Genre genre;
    private String audioUrl;
    private String coverImageUrl;
    private int playCount;
    private boolean explicit;
    private LocalDate releaseDate;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // For search optimization
    private final Set<String> searchableTerms;

    public Song(String id, String title, String artistId, int durationSeconds) {
        this.id = id;
        this.title = title;
        this.artistId = artistId;
        this.durationSeconds = durationSeconds;
        this.playCount = 0;
        this.explicit = false;
        this.searchableTerms = new HashSet<>();
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
        updateSearchableTerms();
    }

    private void updateSearchableTerms() {
        searchableTerms.clear();
        if (title != null) {
            searchableTerms.addAll(Arrays.asList(title.toLowerCase().split("\\s+")));
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtistId() { return artistId; }
    public String getAlbumId() { return albumId; }
    public int getDurationSeconds() { return durationSeconds; }
    public Genre getGenre() { return genre; }
    public String getAudioUrl() { return audioUrl; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public int getPlayCount() { return playCount; }
    public boolean isExplicit() { return explicit; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public Set<String> getSearchableTerms() { return Collections.unmodifiableSet(searchableTerms); }

    // Setters
    public void setTitle(String title) {
        this.title = title;
        updateSearchableTerms();
        this.updatedAt = LocalDateTime.now();
    }

    public void setAlbumId(String albumId) {
        this.albumId = albumId;
        this.updatedAt = LocalDateTime.now();
    }

    public void setDurationSeconds(int durationSeconds) {
        this.durationSeconds = durationSeconds;
        this.updatedAt = LocalDateTime.now();
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
        this.updatedAt = LocalDateTime.now();
    }

    public void setAudioUrl(String audioUrl) {
        this.audioUrl = audioUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setExplicit(boolean explicit) {
        this.explicit = explicit;
        this.updatedAt = LocalDateTime.now();
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementPlayCount() {
        this.playCount++;
        this.updatedAt = LocalDateTime.now();
    }

    public String getFormattedDuration() {
        int minutes = durationSeconds / 60;
        int seconds = durationSeconds % 60;
        return String.format("%d:%02d", minutes, seconds);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Song song = (Song) o;
        return Objects.equals(id, song.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Song{id='%s', title='%s', artistId='%s', duration='%s', genre=%s}",
                id, title, artistId, getFormattedDuration(), genre);
    }
}



