package socialnetwork.repositories.impl;

import socialnetwork.models.Comment;
import socialnetwork.repositories.CommentRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of CommentRepository.
 */
public class InMemoryCommentRepository implements CommentRepository {
    
    private final Map<String, Comment> comments = new ConcurrentHashMap<>();

    @Override
    public Comment save(Comment comment) {
        comments.put(comment.getId(), comment);
        return comment;
    }

    @Override
    public Optional<Comment> findById(String id) {
        return Optional.ofNullable(comments.get(id));
    }

    @Override
    public List<Comment> findByPostId(String postId) {
        return comments.values().stream()
                .filter(c -> c.getPostId().equals(postId) && !c.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Comment> findByAuthorId(String authorId) {
        return comments.values().stream()
                .filter(c -> c.getAuthorId().equals(authorId) && !c.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        Comment comment = comments.get(id);
        if (comment != null) {
            comment.markAsDeleted();
        }
    }
}



