package socialnetwork.services.impl;

import socialnetwork.models.Post;
import socialnetwork.models.User;
import socialnetwork.repositories.PostRepository;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.FriendshipService;
import socialnetwork.services.NewsfeedService;
import socialnetwork.strategies.DefaultPrivacyPolicy;
import socialnetwork.strategies.PrivacyPolicy;
import socialnetwork.strategies.newsfeed.ChronologicalNewsfeedStrategy;
import socialnetwork.strategies.newsfeed.NewsfeedStrategy;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of NewsfeedService.
 * Generates personalized newsfeeds using configurable strategies.
 */
public class NewsfeedServiceImpl implements NewsfeedService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final PrivacyPolicy privacyPolicy;
    private NewsfeedStrategy newsfeedStrategy;

    public NewsfeedServiceImpl(PostRepository postRepository,
                                UserRepository userRepository,
                                FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
        this.privacyPolicy = new DefaultPrivacyPolicy();
        this.newsfeedStrategy = new ChronologicalNewsfeedStrategy(); // Default strategy
    }

    /**
     * Set the newsfeed generation strategy.
     */
    public void setNewsfeedStrategy(NewsfeedStrategy strategy) {
        this.newsfeedStrategy = strategy;
    }

    @Override
    public List<Post> getNewsfeed(String userId, int limit) {
        User viewer = userRepository.findById(userId).orElse(null);
        if (viewer == null) {
            return new ArrayList<>();
        }

        // Get friend IDs plus the user's own ID
        List<String> friendIds = friendshipService.getFriendIds(userId);
        List<String> eligibleAuthors = new ArrayList<>(friendIds);
        eligibleAuthors.add(userId);

        // Get all posts from eligible authors
        List<Post> allPosts = postRepository.findByAuthorIds(eligibleAuthors);

        // Filter by privacy settings
        List<Post> visiblePosts = allPosts.stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> {
                    Set<String> authorFriends = new HashSet<>(
                            friendshipService.getFriendIds(post.getAuthorId()));
                    return privacyPolicy.canViewPost(post, viewer, authorFriends);
                })
                .collect(Collectors.toList());

        // Apply the newsfeed strategy
        return newsfeedStrategy.generateFeed(userId, visiblePosts, limit);
    }
}



