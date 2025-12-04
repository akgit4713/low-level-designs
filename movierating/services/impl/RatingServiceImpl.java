package movierating.services.impl;

import movierating.models.Rating;
import movierating.models.RatingValue;
import movierating.models.User;
import movierating.observers.RatingObserver;
import movierating.services.RatingService;
import movierating.services.UserService;
import movierating.strategies.aggregation.RatingAggregationStrategy;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * Implementation of RatingService with observer support and aggregation strategies.
 * 
 * Single Responsibility: Handles rating operations.
 * Open/Closed: Extends functionality through observers and strategies.
 * Dependency Inversion: Depends on abstractions (strategies, observers).
 */
public class RatingServiceImpl implements RatingService {
    
    private final Map<String, Rating> ratingsById = new ConcurrentHashMap<>();
    private final Map<String, List<String>> ratingsByMovie = new ConcurrentHashMap<>();
    private final Map<String, List<String>> ratingsByUser = new ConcurrentHashMap<>();
    private final Map<String, String> userMovieRatings = new ConcurrentHashMap<>(); // "userId:movieId" -> ratingId
    
    private final UserService userService;
    private final RatingAggregationStrategy aggregationStrategy;
    private final List<RatingObserver> observers = new ArrayList<>();
    
    public RatingServiceImpl(UserService userService, RatingAggregationStrategy aggregationStrategy) {
        this.userService = userService;
        this.aggregationStrategy = aggregationStrategy;
    }
    
    /**
     * Register an observer to receive rating events.
     * @param observer The observer to register
     */
    public void registerObserver(RatingObserver observer) {
        observers.add(observer);
    }
    
    /**
     * Unregister an observer.
     * @param observer The observer to unregister
     */
    public void unregisterObserver(RatingObserver observer) {
        observers.remove(observer);
    }
    
    @Override
    public Rating createRating(String userId, String movieId, RatingValue ratingValue, String review) {
        // Check if user exists
        User user = userService.getUserById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));
        
        // Check if user already rated this movie
        String key = userId + ":" + movieId;
        if (userMovieRatings.containsKey(key)) {
            throw new IllegalArgumentException("User has already rated this movie");
        }
        
        // Create the rating
        Rating rating = new Rating(userId, movieId, ratingValue, review);
        
        // Store the rating
        ratingsById.put(rating.getId(), rating);
        ratingsByMovie.computeIfAbsent(movieId, k -> new ArrayList<>()).add(rating.getId());
        ratingsByUser.computeIfAbsent(userId, k -> new ArrayList<>()).add(rating.getId());
        userMovieRatings.put(key, rating.getId());
        
        // Update user's rating count
        user.incrementRatingsGiven();
        
        // Notify observers
        notifyRatingCreated(rating, user);
        
        return rating;
    }
    
    @Override
    public Rating updateRating(String ratingId, RatingValue newValue, String newReview) {
        Rating rating = ratingsById.get(ratingId);
        if (rating == null) {
            throw new IllegalArgumentException("Rating not found: " + ratingId);
        }
        
        User user = userService.getUserById(rating.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found for rating"));
        
        rating.updateRating(newValue, newReview);
        
        // Notify observers
        notifyRatingUpdated(rating, user);
        
        return rating;
    }
    
    @Override
    public Optional<Rating> getRatingById(String ratingId) {
        return Optional.ofNullable(ratingsById.get(ratingId));
    }
    
    @Override
    public List<Rating> getRatingsForMovie(String movieId) {
        List<String> ratingIds = ratingsByMovie.getOrDefault(movieId, Collections.emptyList());
        return ratingIds.stream()
                .map(ratingsById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public List<Rating> getRatingsByUser(String userId) {
        List<String> ratingIds = ratingsByUser.getOrDefault(userId, Collections.emptyList());
        return ratingIds.stream()
                .map(ratingsById::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }
    
    @Override
    public double getAggregatedRating(String movieId) {
        List<Rating> ratings = getRatingsForMovie(movieId);
        if (ratings.isEmpty()) {
            return 0.0;
        }
        
        // Build user map for weight calculation
        Map<String, User> userMap = new HashMap<>();
        for (Rating rating : ratings) {
            userService.getUserById(rating.getUserId()).ifPresent(u -> userMap.put(u.getId(), u));
        }
        
        return aggregationStrategy.calculateAggregateRating(ratings, userMap);
    }
    
    @Override
    public void voteOnRating(String ratingId, String voterId, boolean isHelpful) {
        Rating rating = ratingsById.get(ratingId);
        if (rating == null) {
            throw new IllegalArgumentException("Rating not found: " + ratingId);
        }
        
        User voter = userService.getUserById(voterId)
                .orElseThrow(() -> new IllegalArgumentException("Voter not found: " + voterId));
        
        User ratingAuthor = userService.getUserById(rating.getUserId())
                .orElseThrow(() -> new IllegalStateException("Rating author not found"));
        
        // Prevent self-voting
        if (ratingAuthor.getId().equals(voterId)) {
            throw new IllegalArgumentException("Users cannot vote on their own ratings");
        }
        
        // Add the vote
        if (isHelpful) {
            rating.addHelpfulVote();
            ratingAuthor.incrementHelpfulVotes();
        } else {
            rating.addNotHelpfulVote();
        }
        
        // Notify observers
        notifyRatingVoted(rating, ratingAuthor, voter, isHelpful);
    }
    
    @Override
    public boolean deleteRating(String ratingId) {
        Rating rating = ratingsById.remove(ratingId);
        if (rating == null) {
            return false;
        }
        
        // Remove from indexes
        String key = rating.getUserId() + ":" + rating.getMovieId();
        userMovieRatings.remove(key);
        
        List<String> movieRatings = ratingsByMovie.get(rating.getMovieId());
        if (movieRatings != null) {
            movieRatings.remove(ratingId);
        }
        
        List<String> userRatings = ratingsByUser.get(rating.getUserId());
        if (userRatings != null) {
            userRatings.remove(ratingId);
        }
        
        // Notify observers
        userService.getUserById(rating.getUserId()).ifPresent(user -> 
            notifyRatingDeleted(rating, user));
        
        return true;
    }
    
    @Override
    public boolean hasUserRatedMovie(String userId, String movieId) {
        return userMovieRatings.containsKey(userId + ":" + movieId);
    }
    
    @Override
    public Optional<Rating> getUserRatingForMovie(String userId, String movieId) {
        String key = userId + ":" + movieId;
        String ratingId = userMovieRatings.get(key);
        return ratingId != null ? Optional.ofNullable(ratingsById.get(ratingId)) : Optional.empty();
    }
    
    // Observer notification methods
    
    private void notifyRatingCreated(Rating rating, User user) {
        for (RatingObserver observer : observers) {
            observer.onRatingCreated(rating, user);
        }
    }
    
    private void notifyRatingUpdated(Rating rating, User user) {
        for (RatingObserver observer : observers) {
            observer.onRatingUpdated(rating, user);
        }
    }
    
    private void notifyRatingDeleted(Rating rating, User user) {
        for (RatingObserver observer : observers) {
            observer.onRatingDeleted(rating, user);
        }
    }
    
    private void notifyRatingVoted(Rating rating, User ratingAuthor, User voter, boolean isHelpful) {
        for (RatingObserver observer : observers) {
            observer.onRatingVoted(rating, ratingAuthor, voter, isHelpful);
        }
    }
}


