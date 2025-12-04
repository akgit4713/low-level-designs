package socialnetwork.strategies;

import socialnetwork.enums.PrivacyLevel;
import socialnetwork.models.Post;
import socialnetwork.models.User;

import java.util.Set;

/**
 * Default implementation of privacy policy.
 * Respects privacy settings on posts and profiles.
 */
public class DefaultPrivacyPolicy implements PrivacyPolicy {

    @Override
    public boolean canViewPost(Post post, User viewer, Set<String> friendIds) {
        // Author can always view their own posts
        if (viewer != null && post.getAuthorId().equals(viewer.getId())) {
            return true;
        }

        switch (post.getPrivacyLevel()) {
            case PUBLIC:
                return true;
            case FRIENDS_ONLY:
                return viewer != null && friendIds.contains(viewer.getId());
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }

    @Override
    public boolean canViewProfile(User profileOwner, User viewer, boolean areFriends) {
        // Owner can always view their own profile
        if (viewer != null && profileOwner.getId().equals(viewer.getId())) {
            return true;
        }

        switch (profileOwner.getProfilePrivacy()) {
            case PUBLIC:
                return true;
            case FRIENDS_ONLY:
                return areFriends;
            case PRIVATE:
                return false;
            default:
                return false;
        }
    }
}



