package onlineshopping.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Represents a product review
 */
public class Review {
    private final String id;
    private final String productId;
    private final String userId;
    private final String userName;
    private final int rating; // 1-5 stars
    private final String title;
    private final String comment;
    private final LocalDateTime createdAt;
    
    private int helpfulCount;
    private boolean verified; // Verified purchase

    public Review(String id, String productId, String userId, String userName, 
                  int rating, String title, String comment) {
        this.id = Objects.requireNonNull(id, "Review ID is required");
        this.productId = Objects.requireNonNull(productId, "Product ID is required");
        this.userId = Objects.requireNonNull(userId, "User ID is required");
        this.userName = Objects.requireNonNull(userName, "User name is required");
        
        if (rating < 1 || rating > 5) {
            throw new IllegalArgumentException("Rating must be between 1 and 5");
        }
        this.rating = rating;
        this.title = title != null ? title : "";
        this.comment = comment != null ? comment : "";
        this.createdAt = LocalDateTime.now();
        this.helpfulCount = 0;
        this.verified = false;
    }

    public String getId() {
        return id;
    }

    public String getProductId() {
        return productId;
    }

    public String getUserId() {
        return userId;
    }

    public String getUserName() {
        return userName;
    }

    public int getRating() {
        return rating;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public int getHelpfulCount() {
        return helpfulCount;
    }

    public void incrementHelpful() {
        this.helpfulCount++;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    /**
     * Get star display (e.g., "★★★★☆")
     */
    public String getStarDisplay() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            sb.append(i < rating ? "★" : "☆");
        }
        return sb.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Review review = (Review) o;
        return Objects.equals(id, review.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return String.format("Review{id='%s', product='%s', rating=%d, by='%s'}", 
            id, productId, rating, userName);
    }
}



