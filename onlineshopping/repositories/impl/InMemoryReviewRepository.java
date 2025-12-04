package onlineshopping.repositories.impl;

import onlineshopping.models.Review;
import onlineshopping.repositories.Repository;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of review repository
 */
public class InMemoryReviewRepository implements Repository<Review, String> {
    
    private final Map<String, Review> reviews = new ConcurrentHashMap<>();

    @Override
    public Review save(Review review) {
        reviews.put(review.getId(), review);
        return review;
    }

    @Override
    public Optional<Review> findById(String id) {
        return Optional.ofNullable(reviews.get(id));
    }

    @Override
    public List<Review> findAll() {
        return new ArrayList<>(reviews.values());
    }

    @Override
    public boolean deleteById(String id) {
        return reviews.remove(id) != null;
    }

    @Override
    public boolean existsById(String id) {
        return reviews.containsKey(id);
    }

    @Override
    public long count() {
        return reviews.size();
    }

    /**
     * Find reviews by product
     */
    public List<Review> findByProduct(String productId) {
        return reviews.values().stream()
            .filter(r -> r.getProductId().equals(productId))
            .sorted(Comparator.comparing(Review::getCreatedAt).reversed())
            .collect(Collectors.toList());
    }

    /**
     * Find reviews by user
     */
    public List<Review> findByUser(String userId) {
        return reviews.values().stream()
            .filter(r -> r.getUserId().equals(userId))
            .collect(Collectors.toList());
    }

    /**
     * Check if user has reviewed a product
     */
    public boolean hasUserReviewedProduct(String userId, String productId) {
        return reviews.values().stream()
            .anyMatch(r -> r.getUserId().equals(userId) && r.getProductId().equals(productId));
    }

    /**
     * Calculate average rating for a product
     */
    public double getAverageRating(String productId) {
        return reviews.values().stream()
            .filter(r -> r.getProductId().equals(productId))
            .mapToInt(Review::getRating)
            .average()
            .orElse(0.0);
    }
}



