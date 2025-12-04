package socialnetwork.repositories.impl;

import socialnetwork.models.Post;
import socialnetwork.repositories.PostRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of PostRepository.
 */
public class InMemoryPostRepository implements PostRepository {
    
    private final Map<String, Post> posts = new ConcurrentHashMap<>();

    @Override
    public Post save(Post post) {
        posts.put(post.getId(), post);
        return post;
    }

    @Override
    public Optional<Post> findById(String id) {
        return Optional.ofNullable(posts.get(id));
    }

    @Override
    public List<Post> findByAuthorId(String authorId) {
        return posts.values().stream()
                .filter(post -> post.getAuthorId().equals(authorId) && !post.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findByAuthorIds(List<String> authorIds) {
        return posts.values().stream()
                .filter(post -> authorIds.contains(post.getAuthorId()) && !post.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public List<Post> findAll() {
        return posts.values().stream()
                .filter(post -> !post.isDeleted())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        Post post = posts.get(id);
        if (post != null) {
            post.markAsDeleted();
        }
    }
}



