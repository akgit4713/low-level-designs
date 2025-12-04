package musicstreaming.models;

import musicstreaming.enums.Genre;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a music album containing multiple songs.
 */
public class Album {
    private final String id;
    private String title;
    private final String artistId;
    private String coverImageUrl;
    private Genre genre;
    private LocalDate releaseDate;
    private final List<String> songIds;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    private Album(Builder builder) {
        this.id = builder.id;
        this.title = builder.title;
        this.artistId = builder.artistId;
        this.coverImageUrl = builder.coverImageUrl;
        this.genre = builder.genre;
        this.releaseDate = builder.releaseDate;
        this.songIds = new CopyOnWriteArrayList<>(builder.songIds);
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getArtistId() { return artistId; }
    public String getCoverImageUrl() { return coverImageUrl; }
    public Genre getGenre() { return genre; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public List<String> getSongIds() { return Collections.unmodifiableList(songIds); }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters
    public void setTitle(String title) {
        this.title = title;
        this.updatedAt = LocalDateTime.now();
    }

    public void setCoverImageUrl(String coverImageUrl) {
        this.coverImageUrl = coverImageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setGenre(Genre genre) {
        this.genre = genre;
        this.updatedAt = LocalDateTime.now();
    }

    public void setReleaseDate(LocalDate releaseDate) {
        this.releaseDate = releaseDate;
        this.updatedAt = LocalDateTime.now();
    }

    public void addSong(String songId) {
        if (!songIds.contains(songId)) {
            songIds.add(songId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void removeSong(String songId) {
        if (songIds.remove(songId)) {
            this.updatedAt = LocalDateTime.now();
        }
    }

    public int getSongCount() {
        return songIds.size();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Album album = (Album) o;
        return Objects.equals(id, album.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Album{id='%s', title='%s', artistId='%s', songs=%d}",
                id, title, artistId, songIds.size());
    }

    // Builder Pattern for complex album creation
    public static class Builder {
        private final String id;
        private final String title;
        private final String artistId;
        private String coverImageUrl;
        private Genre genre;
        private LocalDate releaseDate;
        private final List<String> songIds = new ArrayList<>();

        public Builder(String id, String title, String artistId) {
            this.id = id;
            this.title = title;
            this.artistId = artistId;
        }

        public Builder coverImageUrl(String coverImageUrl) {
            this.coverImageUrl = coverImageUrl;
            return this;
        }

        public Builder genre(Genre genre) {
            this.genre = genre;
            return this;
        }

        public Builder releaseDate(LocalDate releaseDate) {
            this.releaseDate = releaseDate;
            return this;
        }

        public Builder addSong(String songId) {
            this.songIds.add(songId);
            return this;
        }

        public Builder addSongs(List<String> songIds) {
            this.songIds.addAll(songIds);
            return this;
        }

        public Album build() {
            return new Album(this);
        }
    }
}



