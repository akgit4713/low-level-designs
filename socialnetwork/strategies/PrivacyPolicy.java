package socialnetwork.strategies;

import socialnetwork.models.Post;
import socialnetwork.models.User;

import java.util.Set;

/**
 * Strategy interface for privacy policy decisions.
 * Determines if a viewer can access content.
 */
public interface PrivacyPolicy {
    
    /**
     * Check if a viewer can view a post.
     * 
     * @param post The post being viewed
     * @param viewer The user attempting to view (can be null for anonymous)
     * @param friendIds Set of friend IDs for the post author
     * @return true if viewer has access
     */
    boolean canViewPost(Post post, User viewer, Set<String> friendIds);
    
    /**
     * Check if a viewer can view a user's profile.
     * 
     * @param profileOwner The owner of the profile
     * @param viewer The user attempting to view (can be null for anonymous)
     * @param areFriends Whether viewer and owner are friends
     * @return true if viewer has access
     */
    boolean canViewProfile(User profileOwner, User viewer, boolean areFriends);
}



