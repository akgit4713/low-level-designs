package movierating.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a rating given by a user to a movie.
 * 
 * Single Responsibility: Only handles rating data.
 * Weight calculation is delegated to strategies.
 */
public class Rating {
    private final String id;
    private final String userId;
    private final String movieId;
    private RatingValue ratingValue;
    private String review;
    private int helpfulVotes;
    private int notHelpfulVotes;
    private final LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Rating(String userId, String movieId, RatingValue ratingValue, String review) {
        this.id = UUID.randomUUID().toString();
        this.userId = userId;
        this.movieId = movieId;
        this.ratingValue = ratingValue;
        this.review = review;
        this.helpfulVotes = 0;
        this.notHelpfulVotes = 0;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUserId() {
        return userId;
    }

    public String getMovieId() {
        return movieId;
    }

    public RatingValue getRatingValue() {
        return ratingValue;
    }

    public String getReview() {
        return review;
    }

    public int getHelpfulVotes() {
        return helpfulVotes;
    }

    public int getNotHelpfulVotes() {
        return notHelpfulVotes;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    // Setters and mutators
    public void updateRating(RatingValue newValue, String newReview) {
        this.ratingValue = newValue;
        this.review = newReview;
        this.updatedAt = LocalDateTime.now();
    }

    public void addHelpfulVote() {
        this.helpfulVotes++;
    }

    public void addNotHelpfulVote() {
        this.notHelpfulVotes++;
    }

    /**
     * Calculate helpfulness score based on votes.
     * @return Helpfulness ratio (0.0 to 1.0)
     */
    public double getHelpfulnessScore() {
        int totalVotes = helpfulVotes + notHelpfulVotes;
        if (totalVotes == 0) {
            return 0.5; // Neutral score if no votes
        }
        return (double) helpfulVotes / totalVotes;
    }

    @Override
    public String toString() {
        return String.format("Rating{movie=%s, value=%s, helpful=%d/%d}",
                movieId, ratingValue, helpfulVotes, helpfulVotes + notHelpfulVotes);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rating rating = (Rating) o;
        return id.equals(rating.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


