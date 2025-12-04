package socialnetwork.strategies.newsfeed;

import socialnetwork.models.Post;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Sorts posts in reverse chronological order (newest first).
 * This is the default Facebook-like newsfeed behavior.
 */
public class ChronologicalNewsfeedStrategy implements NewsfeedStrategy {

    @Override
    public List<Post> generateFeed(String userId, List<Post> posts, int limit) {
        return posts.stream()
                .filter(post -> !post.isDeleted())
                .sorted(Comparator.comparing(Post::getCreatedAt).reversed())
                .limit(limit)
                .collect(Collectors.toList());
    }

    @Override
    public String getName() {
        return "Chronological";
    }
}



