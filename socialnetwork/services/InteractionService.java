package socialnetwork.services;

import socialnetwork.models.Comment;
import socialnetwork.models.Like;
import socialnetwork.models.User;

import java.util.List;

/**
 * Service interface for post interactions (likes, comments).
 */
public interface InteractionService {
    
    /**
     * Like a post.
     */
    Like likePost(String postId, String userId);
    
    /**
     * Unlike a post.
     */
    void unlikePost(String postId, String userId);
    
    /**
     * Check if user has liked a post.
     */
    boolean hasLiked(String postId, String userId);
    
    /**
     * Get all likes on a post.
     */
    List<Like> getLikes(String postId);
    
    /**
     * Get users who liked a post.
     */
    List<User> getUsersWhoLiked(String postId);
    
    /**
     * Add a comment to a post.
     */
    Comment addComment(String postId, String authorId, String content);
    
    /**
     * Update a comment.
     */
    Comment updateComment(String commentId, String userId, String content);
    
    /**
     * Delete a comment.
     */
    void deleteComment(String commentId, String userId);
    
    /**
     * Get all comments on a post.
     */
    List<Comment> getComments(String postId);
}



