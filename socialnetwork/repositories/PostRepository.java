package socialnetwork.repositories;

import socialnetwork.models.Post;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Post data access.
 */
public interface PostRepository {
    
    Post save(Post post);
    
    Optional<Post> findById(String id);
    
    List<Post> findByAuthorId(String authorId);
    
    List<Post> findByAuthorIds(List<String> authorIds);
    
    List<Post> findAll();
    
    void delete(String id);
}



