package bookmyshow.models;

import bookmyshow.enums.Genre;
import java.time.Duration;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Represents a movie in the system.
 */
public class Movie {
    private final String id;
    private String title;
    private String description;
    private Duration duration;
    private String language;
    private LocalDate releaseDate;
    private Set<Genre> genres;
    private String director;
    private Set<String> cast;
    private String posterUrl;
    private double rating;
    private int ratingCount;

    public Movie(String title, String description, Duration duration, String language, LocalDate releaseDate) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.description = description;
        this.duration = duration;
        this.language = language;
        this.releaseDate = releaseDate;
        this.genres = new HashSet<>();
        this.cast = new HashSet<>();
        this.rating = 0.0;
        this.ratingCount = 0;
    }

    // Builder pattern for optional fields
    public static class Builder {
        private final String title;
        private final Duration duration;
        private String description = "";
        private String language = "English";
        private LocalDate releaseDate = LocalDate.now();
        private Set<Genre> genres = new HashSet<>();
        private String director = "";
        private Set<String> cast = new HashSet<>();
        private String posterUrl = "";

        public Builder(String title, Duration duration) {
            this.title = title;
            this.duration = duration;
        }

        public Builder description(String description) { this.description = description; return this; }
        public Builder language(String language) { this.language = language; return this; }
        public Builder releaseDate(LocalDate date) { this.releaseDate = date; return this; }
        public Builder genre(Genre genre) { this.genres.add(genre); return this; }
        public Builder genres(Set<Genre> genres) { this.genres = genres; return this; }
        public Builder director(String director) { this.director = director; return this; }
        public Builder cast(Set<String> cast) { this.cast = cast; return this; }
        public Builder posterUrl(String url) { this.posterUrl = url; return this; }

        public Movie build() {
            Movie movie = new Movie(title, description, duration, language, releaseDate);
            movie.genres = this.genres;
            movie.director = this.director;
            movie.cast = this.cast;
            movie.posterUrl = this.posterUrl;
            return movie;
        }
    }

    // Getters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public Duration getDuration() { return duration; }
    public String getLanguage() { return language; }
    public LocalDate getReleaseDate() { return releaseDate; }
    public Set<Genre> getGenres() { return new HashSet<>(genres); }
    public String getDirector() { return director; }
    public Set<String> getCast() { return new HashSet<>(cast); }
    public String getPosterUrl() { return posterUrl; }
    public double getRating() { return rating; }
    public int getRatingCount() { return ratingCount; }

    // Setters
    public void setTitle(String title) { this.title = title; }
    public void setDescription(String description) { this.description = description; }
    public void setDuration(Duration duration) { this.duration = duration; }
    public void setLanguage(String language) { this.language = language; }
    public void setReleaseDate(LocalDate releaseDate) { this.releaseDate = releaseDate; }
    public void setDirector(String director) { this.director = director; }
    public void setPosterUrl(String posterUrl) { this.posterUrl = posterUrl; }

    public void addGenre(Genre genre) { this.genres.add(genre); }
    public void removeGenre(Genre genre) { this.genres.remove(genre); }
    public void addCastMember(String member) { this.cast.add(member); }

    public void addRating(double newRating) {
        this.rating = ((this.rating * this.ratingCount) + newRating) / (this.ratingCount + 1);
        this.ratingCount++;
    }

    @Override
    public String toString() {
        return String.format("Movie{id='%s', title='%s', duration=%s, language='%s', rating=%.1f}", 
            id, title, duration, language, rating);
    }
}



