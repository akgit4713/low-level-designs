package socialnetwork.repositories.impl;

import socialnetwork.models.Like;
import socialnetwork.repositories.LikeRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of LikeRepository.
 */
public class InMemoryLikeRepository implements LikeRepository {
    
    private final Map<String, Like> likes = new ConcurrentHashMap<>();
    // Composite key: postId:userId -> likeId
    private final Map<String, String> userPostIndex = new ConcurrentHashMap<>();

    private String compositeKey(String postId, String userId) {
        return postId + ":" + userId;
    }

    @Override
    public Like save(Like like) {
        likes.put(like.getId(), like);
        userPostIndex.put(compositeKey(like.getPostId(), like.getUserId()), like.getId());
        return like;
    }

    @Override
    public Optional<Like> findById(String id) {
        return Optional.ofNullable(likes.get(id));
    }

    @Override
    public List<Like> findByPostId(String postId) {
        return likes.values().stream()
                .filter(like -> like.getPostId().equals(postId))
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Like> findByPostIdAndUserId(String postId, String userId) {
        String likeId = userPostIndex.get(compositeKey(postId, userId));
        if (likeId == null) return Optional.empty();
        return findById(likeId);
    }

    @Override
    public boolean existsByPostIdAndUserId(String postId, String userId) {
        return userPostIndex.containsKey(compositeKey(postId, userId));
    }

    @Override
    public void delete(String id) {
        Like like = likes.remove(id);
        if (like != null) {
            userPostIndex.remove(compositeKey(like.getPostId(), like.getUserId()));
        }
    }

    @Override
    public void deleteByPostIdAndUserId(String postId, String userId) {
        String likeId = userPostIndex.get(compositeKey(postId, userId));
        if (likeId != null) {
            delete(likeId);
        }
    }
}



