package fooddelivery.models;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Customer review for a restaurant or delivery.
 */
public class Review {
    private final String id;
    private final String customerId;
    private final String orderId;
    private final String restaurantId;
    private double foodRating;
    private double deliveryRating;
    private String comment;
    private final LocalDateTime createdAt;

    public Review(String id, String customerId, String orderId, String restaurantId) {
        this.id = id;
        this.customerId = customerId;
        this.orderId = orderId;
        this.restaurantId = restaurantId;
        this.createdAt = LocalDateTime.now();
    }

    public String getId() {
        return id;
    }

    public String getCustomerId() {
        return customerId;
    }

    public String getOrderId() {
        return orderId;
    }

    public String getRestaurantId() {
        return restaurantId;
    }

    public double getFoodRating() {
        return foodRating;
    }

    public void setFoodRating(double foodRating) {
        this.foodRating = foodRating;
    }

    public double getDeliveryRating() {
        return deliveryRating;
    }

    public void setDeliveryRating(double deliveryRating) {
        this.deliveryRating = deliveryRating;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
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
}



