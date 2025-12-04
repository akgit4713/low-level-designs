package movierating.models;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Represents a user in the movie rating system.
 * Users have levels that affect the weight of their ratings.
 * 
 * Single Responsibility: Only handles user data and basic state.
 */
public class User {
    private final String id;
    private final String username;
    private final String email;
    private UserLevel level;
    private int totalRatingsGiven;
    private int helpfulVotesReceived;
    private final LocalDateTime createdAt;
    private LocalDateTime lastActivityAt;

    public User(String username, String email) {
        this.id = UUID.randomUUID().toString();
        this.username = username;
        this.email = email;
        this.level = UserLevel.NOVICE;
        this.totalRatingsGiven = 0;
        this.helpfulVotesReceived = 0;
        this.createdAt = LocalDateTime.now();
        this.lastActivityAt = LocalDateTime.now();
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getEmail() {
        return email;
    }

    public UserLevel getLevel() {
        return level;
    }

    public int getTotalRatingsGiven() {
        return totalRatingsGiven;
    }

    public int getHelpfulVotesReceived() {
        return helpfulVotesReceived;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getLastActivityAt() {
        return lastActivityAt;
    }

    // Setters and mutators
    public void setLevel(UserLevel level) {
        this.level = level;
    }

    public void incrementRatingsGiven() {
        this.totalRatingsGiven++;
        this.lastActivityAt = LocalDateTime.now();
    }

    public void incrementHelpfulVotes() {
        this.helpfulVotesReceived++;
    }

    public void decrementHelpfulVotes() {
        if (this.helpfulVotesReceived > 0) {
            this.helpfulVotesReceived--;
        }
    }

    @Override
    public String toString() {
        return String.format("User{username='%s', level=%s, ratings=%d, helpfulVotes=%d}",
                username, level.getDisplayName(), totalRatingsGiven, helpfulVotesReceived);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.equals(user.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}


