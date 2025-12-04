package movierating.models;

import java.time.LocalDate;
import java.util.UUID;

/**
 * Represents a movie in the rating system.
 * 
 * Single Responsibility: Only handles movie metadata.
 * Rating aggregation is handled separately by strategies.
 */
public class Movie {
    private final String id;
    private final String title;
    private final String director;
    private final int releaseYear;
    private final String genre;
    private final String description;

    public Movie(String title, String director, int releaseYear, String genre, String description) {
        this.id = UUID.randomUUID().toString();
        this.title = title;
        this.director = director;
        this.releaseYear = releaseYear;
        this.genre = genre;
        this.description = description;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDirector() {
        return director;
    }

    public int getReleaseYear() {
        return releaseYear;
    }

    public String getGenre() {
        return genre;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return String.format("Movie{title='%s', director='%s', year=%d, genre='%s'}",
                title, director, releaseYear, genre);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Movie movie = (Movie) o;
        return id.equals(movie.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


