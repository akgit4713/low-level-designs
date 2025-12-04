package socialnetwork.services;

import socialnetwork.models.Post;

import java.util.List;

/**
 * Service interface for newsfeed operations.
 */
public interface NewsfeedService {
    
    /**
     * Get newsfeed for a user.
     * Returns posts from friends and the user, sorted appropriately.
     * 
     * @param userId The user requesting the feed
     * @param limit Maximum number of posts
     * @return List of posts for the newsfeed
     */
    List<Post> getNewsfeed(String userId, int limit);
    
    /**
     * Get newsfeed with default limit.
     */
    default List<Post> getNewsfeed(String userId) {
        return getNewsfeed(userId, 20);
    }
}



