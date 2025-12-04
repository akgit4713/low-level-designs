package socialnetwork.services.impl;

import socialnetwork.exceptions.PostNotFoundException;
import socialnetwork.exceptions.UnauthorizedException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.exceptions.ValidationException;
import socialnetwork.factories.NotificationFactory;
import socialnetwork.models.Comment;
import socialnetwork.models.Like;
import socialnetwork.models.Post;
import socialnetwork.models.User;
import socialnetwork.repositories.CommentRepository;
import socialnetwork.repositories.LikeRepository;
import socialnetwork.repositories.PostRepository;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.InteractionService;
import socialnetwork.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of InteractionService.
 * Handles likes and comments on posts.
 */
public class InteractionServiceImpl implements InteractionService {
    
    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public InteractionServiceImpl(PostRepository postRepository,
                                   LikeRepository likeRepository,
                                   CommentRepository commentRepository,
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.postRepository = postRepository;
        this.likeRepository = likeRepository;
        this.commentRepository = commentRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public Like likePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.isDeleted()) {
            throw new PostNotFoundException(postId);
        }

        // Check if already liked
        if (likeRepository.existsByPostIdAndUserId(postId, userId)) {
            return likeRepository.findByPostIdAndUserId(postId, userId).get();
        }

        Like like = new Like(postId, userId);
        likeRepository.save(like);
        post.addLike(like.getId());
        postRepository.save(post);

        // Notify post owner (unless liking own post)
        if (!post.getAuthorId().equals(userId)) {
            User liker = userRepository.findById(userId)
                    .orElseThrow(() -> new UserNotFoundException(userId));
            notificationService.sendNotification(
                    NotificationFactory.createLike(post.getAuthorId(), liker, postId));
        }

        return like;
    }

    @Override
    public void unlikePost(String postId, String userId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        likeRepository.findByPostIdAndUserId(postId, userId).ifPresent(like -> {
            post.removeLike(like.getId());
            postRepository.save(post);
            likeRepository.delete(like.getId());
        });
    }

    @Override
    public boolean hasLiked(String postId, String userId) {
        return likeRepository.existsByPostIdAndUserId(postId, userId);
    }

    @Override
    public List<Like> getLikes(String postId) {
        return likeRepository.findByPostId(postId);
    }

    @Override
    public List<User> getUsersWhoLiked(String postId) {
        List<String> userIds = likeRepository.findByPostId(postId).stream()
                .map(Like::getUserId)
                .collect(Collectors.toList());
        return userRepository.findByIds(userIds);
    }

    @Override
    public Comment addComment(String postId, String authorId, String content) {
        if (content == null || content.isBlank()) {
            throw ValidationException.emptyContent();
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new PostNotFoundException(postId));

        if (post.isDeleted()) {
            throw new PostNotFoundException(postId);
        }

        Comment comment = Comment.builder()
                .postId(postId)
                .authorId(authorId)
                .content(content)
                .build();

        commentRepository.save(comment);
        post.addComment(comment.getId());
        postRepository.save(post);

        // Notify post owner (unless commenting on own post)
        if (!post.getAuthorId().equals(authorId)) {
            User commenter = userRepository.findById(authorId)
                    .orElseThrow(() -> new UserNotFoundException(authorId));
            notificationService.sendNotification(
                    NotificationFactory.createComment(post.getAuthorId(), commenter, postId, content));
        }

        return comment;
    }

    @Override
    public Comment updateComment(String commentId, String userId, String content) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("Comment not found: " + commentId));

        if (!comment.getAuthorId().equals(userId)) {
            throw new UnauthorizedException("Only the author can edit this comment");
        }

        if (content == null || content.isBlank()) {
            throw ValidationException.emptyContent();
        }

        comment.setContent(content);
        return commentRepository.save(comment);
    }

    @Override
    public void deleteComment(String commentId, String userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new PostNotFoundException("Comment not found: " + commentId));

        // Allow author or post owner to delete
        Post post = postRepository.findById(comment.getPostId())
                .orElseThrow(() -> new PostNotFoundException(comment.getPostId()));

        if (!comment.getAuthorId().equals(userId) && !post.getAuthorId().equals(userId)) {
            throw new UnauthorizedException("You don't have permission to delete this comment");
        }

        post.removeComment(commentId);
        postRepository.save(post);
        commentRepository.delete(commentId);
    }

    @Override
    public List<Comment> getComments(String postId) {
        return commentRepository.findByPostId(postId);
    }
}



