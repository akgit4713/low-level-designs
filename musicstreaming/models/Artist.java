package musicstreaming.models;

import musicstreaming.enums.Genre;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Represents a music artist/band in the system.
 */
public class Artist {
    private final String id;
    private String name;
    private String bio;
    private String imageUrl;
    private final List<Genre> genres;
    private final List<String> albumIds;
    private int monthlyListeners;
    private boolean verified;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Artist(String id, String name) {
        this.id = id;
        this.name = name;
        this.genres = new CopyOnWriteArrayList<>();
        this.albumIds = new CopyOnWriteArrayList<>();
        this.monthlyListeners = 0;
        this.verified = false;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getBio() { return bio; }
    public String getImageUrl() { return imageUrl; }
    public List<Genre> getGenres() { return Collections.unmodifiableList(genres); }
    public List<String> getAlbumIds() { return Collections.unmodifiableList(albumIds); }
    public int getMonthlyListeners() { return monthlyListeners; }
    public boolean isVerified() { return verified; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    // Setters with timestamp update
    public void setName(String name) {
        this.name = name;
        this.updatedAt = LocalDateTime.now();
    }

    public void setBio(String bio) {
        this.bio = bio;
        this.updatedAt = LocalDateTime.now();
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        this.updatedAt = LocalDateTime.now();
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
        this.updatedAt = LocalDateTime.now();
    }

    public void incrementMonthlyListeners() {
        this.monthlyListeners++;
        this.updatedAt = LocalDateTime.now();
    }

    public void addGenre(Genre genre) {
        if (!genres.contains(genre)) {
            genres.add(genre);
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void addAlbum(String albumId) {
        if (!albumIds.contains(albumId)) {
            albumIds.add(albumId);
            this.updatedAt = LocalDateTime.now();
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Artist artist = (Artist) o;
        return Objects.equals(id, artist.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Artist{id='%s', name='%s', verified=%s, monthlyListeners=%d}",
                id, name, verified, monthlyListeners);
    }
}



