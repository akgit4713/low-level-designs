package socialnetwork.services.impl;

import socialnetwork.enums.PostType;
import socialnetwork.enums.PrivacyLevel;
import socialnetwork.exceptions.PostNotFoundException;
import socialnetwork.exceptions.UnauthorizedException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.exceptions.ValidationException;
import socialnetwork.models.Post;
import socialnetwork.models.User;
import socialnetwork.repositories.PostRepository;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.FriendshipService;
import socialnetwork.services.PostService;
import socialnetwork.strategies.DefaultPrivacyPolicy;
import socialnetwork.strategies.PrivacyPolicy;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of PostService.
 * Handles post creation, retrieval, and management.
 */
public class PostServiceImpl implements PostService {
    
    private final PostRepository postRepository;
    private final UserRepository userRepository;
    private final FriendshipService friendshipService;
    private final PrivacyPolicy privacyPolicy;

    public PostServiceImpl(PostRepository postRepository, 
                           UserRepository userRepository,
                           FriendshipService friendshipService) {
        this.postRepository = postRepository;
        this.userRepository = userRepository;
        this.friendshipService = friendshipService;
        this.privacyPolicy = new DefaultPrivacyPolicy();
    }

    @Override
    public Post createPost(String authorId, String content, PostType type, 
                           String mediaUrl, PrivacyLevel privacyLevel) {
        if (content == null || content.isBlank()) {
            throw ValidationException.emptyContent();
        }

        User author = userRepository.findById(authorId)
                .orElseThrow(() -> new UserNotFoundException(authorId));

        PrivacyLevel privacy = privacyLevel != null ? privacyLevel : author.getDefaultPostPrivacy();

        Post post = Post.builder()
                .authorId(authorId)
                .content(content)
                .type(type != null ? type : PostType.TEXT)
                .mediaUrl(mediaUrl)
                .privacyLevel(privacy)
                .build();

        return postRepository.save(post);
    }

    @Override
    public Post createTextPost(String authorId, String content) {
        return createPost(authorId, content, PostType.TEXT, null, null);
    }

    @Override
    public Post getPost(String postId, String viewerId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.isDeleted()) {
            throw new PostNotFoundException(postId);
        }

        User viewer = viewerId != null ? userRepository.findById(viewerId).orElse(null) : null;
        Set<String> friendIds = new HashSet<>(friendshipService.getFriendIds(post.getAuthorId()));

        if (!privacyPolicy.canViewPost(post, viewer, friendIds)) {
            throw UnauthorizedException.cannotViewPost();
        }

        return post;
    }

    @Override
    public List<Post> getPostsByUser(String userId, String viewerId) {
        User viewer = viewerId != null ? userRepository.findById(viewerId).orElse(null) : null;
        Set<String> friendIds = new HashSet<>(friendshipService.getFriendIds(userId));

        return postRepository.findByAuthorId(userId).stream()
                .filter(post -> !post.isDeleted())
                .filter(post -> privacyPolicy.canViewPost(post, viewer, friendIds))
                .collect(Collectors.toList());
    }

    @Override
    public Post updatePost(String postId, String userId, String content) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(userId)) {
            throw UnauthorizedException.cannotModifyPost();
        }

        if (content == null || content.isBlank()) {
            throw ValidationException.emptyContent();
        }

        post.setContent(content);
        return postRepository.save(post);
    }

    @Override
    public void deletePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(userId)) {
            throw UnauthorizedException.cannotModifyPost();
        }

        postRepository.delete(postId);
    }

    @Override
    public Post updatePostPrivacy(String postId, String userId, PrivacyLevel privacy) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (!post.getAuthorId().equals(userId)) {
            throw UnauthorizedException.cannotModifyPost();
        }

        post.setPrivacyLevel(privacy);
        return postRepository.save(post);
    }
}



