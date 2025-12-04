package socialnetwork.repositories;

import socialnetwork.models.Comment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Comment data access.
 */
public interface CommentRepository {
    
    Comment save(Comment comment);
    
    Optional<Comment> findById(String id);
    
    List<Comment> findByPostId(String postId);
    
    List<Comment> findByAuthorId(String authorId);
    
    void delete(String id);
}



