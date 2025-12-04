package socialnetwork.services;

import socialnetwork.enums.PostType;
import socialnetwork.enums.PrivacyLevel;
import socialnetwork.models.Post;

import java.util.List;

/**
 * Service interface for post operations.
 */
public interface PostService {
    
    /**
     * Create a new post.
     */
    Post createPost(String authorId, String content, PostType type, 
                    String mediaUrl, PrivacyLevel privacyLevel);
    
    /**
     * Create a text post with default privacy.
     */
    Post createTextPost(String authorId, String content);
    
    /**
     * Get a post by ID (with privacy check).
     */
    Post getPost(String postId, String viewerId);
    
    /**
     * Get all posts by a user.
     */
    List<Post> getPostsByUser(String userId, String viewerId);
    
    /**
     * Update a post.
     */
    Post updatePost(String postId, String userId, String content);
    
    /**
     * Delete a post.
     */
    void deletePost(String postId, String userId);
    
    /**
     * Update post privacy.
     */
    Post updatePostPrivacy(String postId, String userId, PrivacyLevel privacy);
}



