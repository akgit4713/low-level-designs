package socialnetwork.exceptions;

/**
 * Exception thrown when a post is not found.
 */
public class PostNotFoundException extends SocialNetworkException {
    
    public PostNotFoundException(String postId) {
        super("Post not found with ID: " + postId);
    }
}



