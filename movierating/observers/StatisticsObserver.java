package movierating.observers;

import movierating.models.Rating;
import movierating.models.User;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Observer that collects statistics about rating activity.
 * 
 * Observer Pattern: Collects data without interfering with the rating process.
 * Single Responsibility: Only handles statistics collection.
 */
public class StatisticsObserver implements RatingObserver {
    
    private final AtomicLong totalRatingsCreated = new AtomicLong(0);
    private final AtomicLong totalRatingsUpdated = new AtomicLong(0);
    private final AtomicLong totalRatingsDeleted = new AtomicLong(0);
    private final AtomicLong totalHelpfulVotes = new AtomicLong(0);
    private final AtomicLong totalNotHelpfulVotes = new AtomicLong(0);
    
    private final Map<String, Long> ratingsPerMovie = new HashMap<>();
    private final Map<String, Long> ratingsPerUser = new HashMap<>();
    
    @Override
    public void onRatingCreated(Rating rating, User user) {
        totalRatingsCreated.incrementAndGet();
        ratingsPerMovie.merge(rating.getMovieId(), 1L, Long::sum);
        ratingsPerUser.merge(user.getId(), 1L, Long::sum);
    }
    
    @Override
    public void onRatingUpdated(Rating rating, User user) {
        totalRatingsUpdated.incrementAndGet();
    }
    
    @Override
    public void onRatingDeleted(Rating rating, User user) {
        totalRatingsDeleted.incrementAndGet();
        ratingsPerMovie.merge(rating.getMovieId(), -1L, Long::sum);
        ratingsPerUser.merge(user.getId(), -1L, Long::sum);
    }
    
    @Override
    public void onRatingVoted(Rating rating, User ratingAuthor, User voter, boolean isHelpful) {
        if (isHelpful) {
            totalHelpfulVotes.incrementAndGet();
        } else {
            totalNotHelpfulVotes.incrementAndGet();
        }
    }
    
    // Getters for statistics
    
    public long getTotalRatingsCreated() {
        return totalRatingsCreated.get();
    }
    
    public long getTotalRatingsUpdated() {
        return totalRatingsUpdated.get();
    }
    
    public long getTotalRatingsDeleted() {
        return totalRatingsDeleted.get();
    }
    
    public long getTotalHelpfulVotes() {
        return totalHelpfulVotes.get();
    }
    
    public long getTotalNotHelpfulVotes() {
        return totalNotHelpfulVotes.get();
    }
    
    public long getRatingsForMovie(String movieId) {
        return ratingsPerMovie.getOrDefault(movieId, 0L);
    }
    
    public long getRatingsForUser(String userId) {
        return ratingsPerUser.getOrDefault(userId, 0L);
    }
    
    public double getAverageVoteHelpfulness() {
        long total = totalHelpfulVotes.get() + totalNotHelpfulVotes.get();
        if (total == 0) return 0.0;
        return (double) totalHelpfulVotes.get() / total;
    }
    
    @Override
    public String toString() {
        return String.format(
            "Statistics{created=%d, updated=%d, deleted=%d, helpful=%d, notHelpful=%d}",
            totalRatingsCreated.get(), totalRatingsUpdated.get(), totalRatingsDeleted.get(),
            totalHelpfulVotes.get(), totalNotHelpfulVotes.get()
        );
    }
}


