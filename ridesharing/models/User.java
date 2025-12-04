package ridesharing.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract base class for all users in the system.
 */
public abstract class User {
    protected final String userId;
    protected String name;
    protected String email;
    protected String phone;
    protected final LocalDateTime createdAt;
    protected double rating;
    protected int totalRatings;

    protected User(String userId, String name, String email, String phone) {
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.name = Objects.requireNonNull(name, "Name is required");
        this.email = email;
        this.phone = phone;
        this.createdAt = LocalDateTime.now();
        this.rating = 5.0; // Default rating
        this.totalRatings = 0;
    }

    public String getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public double getRating() {
        return rating;
    }

    public int getTotalRatings() {
        return totalRatings;
    }

    /**
     * Updates the user's rating with a new rating value.
     * Uses incremental average calculation.
     */
    public void addRating(double newRating) {
        if (newRating < 1 || newRating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        totalRatings++;
        rating = rating + (newRating - rating) / totalRatings;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userId, user.userId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId);
    }
}



