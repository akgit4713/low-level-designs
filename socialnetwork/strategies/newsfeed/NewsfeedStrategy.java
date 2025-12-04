package socialnetwork.strategies.newsfeed;

import socialnetwork.models.Post;

import java.util.List;

/**
 * Strategy interface for generating newsfeeds.
 * Different implementations can provide different algorithms
 * for sorting and filtering posts.
 */
public interface NewsfeedStrategy {
    
    /**
     * Generate a newsfeed for a user.
     * 
     * @param userId The user requesting the feed
     * @param posts All eligible posts
     * @param limit Maximum number of posts to return
     * @return Sorted and filtered list of posts
     */
    List<Post> generateFeed(String userId, List<Post> posts, int limit);
    
    /**
     * Get the name of this strategy.
     */
    String getName();
}



