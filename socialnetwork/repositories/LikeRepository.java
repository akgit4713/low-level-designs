package socialnetwork.repositories;

import socialnetwork.models.Like;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Like data access.
 */
public interface LikeRepository {
    
    Like save(Like like);
    
    Optional<Like> findById(String id);
    
    List<Like> findByPostId(String postId);
    
    Optional<Like> findByPostIdAndUserId(String postId, String userId);
    
    boolean existsByPostIdAndUserId(String postId, String userId);
    
    void delete(String id);
    
    void deleteByPostIdAndUserId(String postId, String userId);
}



