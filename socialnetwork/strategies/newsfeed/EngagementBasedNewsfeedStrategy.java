package socialnetwork.strategies.newsfeed;

import socialnetwork.models.Post;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sorts posts based on engagement (likes + comments) with time decay.
 * More recent posts with higher engagement rank higher.
 */
public class EngagementBasedNewsfeedStrategy implements NewsfeedStrategy {

    private static final double TIME_DECAY_FACTOR = 0.1;

    @Override
    public List<Post> generateFeed(String userId, List<Post> posts, int limit) {
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparingDouble(this::calculateScore).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    private double calculateScore(Post post) {
        int engagement = post.getLikeCount() + (post.getCommentCount() * 2); // Comments weighted more
        long hoursAgo = ChronoUnit.HOURS.between(post.getCreatedAt(), LocalDateTime.now());
        
        // Score decays over time
        double timeDecay = Math.exp(-TIME_DECAY_FACTOR * hoursAgo);
        return engagement * timeDecay + timeDecay; // Add base time score
    }

    @Override
    public String getName() {
        return "Engagement-Based";
    }
}



