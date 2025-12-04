package socialnetwork;

import socialnetwork.enums.PostType;
import socialnetwork.enums.PrivacyLevel;
import socialnetwork.models.*;
import socialnetwork.observers.EmailNotificationObserver;
import socialnetwork.observers.InAppNotificationObserver;
import socialnetwork.observers.NotificationObserver;
import socialnetwork.observers.PushNotificationObserver;
import socialnetwork.repositories.*;
import socialnetwork.repositories.impl.*;
import socialnetwork.services.*;
import socialnetwork.services.impl.*;
import socialnetwork.strategies.newsfeed.NewsfeedStrategy;

import java.util.List;

/**
 * Facade class that provides a unified API for the Social Network.
 * Encapsulates all services and provides a simple interface for clients.
 */
public class SocialNetwork {
    
    // Repositories
    private final UserRepository userRepository;
    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final LikeRepository likeRepository;
    private final FriendRequestRepository friendRequestRepository;
    private final NotificationRepository notificationRepository;
    private final SessionRepository sessionRepository;

    // Services
    private final AuthService authService;
    private final UserService userService;
    private final FriendshipService friendshipService;
    private final PostService postService;
    private final NewsfeedServiceImpl newsfeedService;
    private final InteractionService interactionService;
    private final NotificationService notificationService;

    /**
     * Create a new SocialNetwork instance with default in-memory repositories.
     */
    public SocialNetwork() {
        // Initialize repositories
        this.userRepository = new InMemoryUserRepository();
        this.postRepository = new InMemoryPostRepository();
        this.commentRepository = new InMemoryCommentRepository();
        this.likeRepository = new InMemoryLikeRepository();
        this.friendRequestRepository = new InMemoryFriendRequestRepository();
        this.notificationRepository = new InMemoryNotificationRepository();
        this.sessionRepository = new InMemorySessionRepository();

        // Initialize services with dependency injection
        this.notificationService = new NotificationServiceImpl(notificationRepository);
        
        this.authService = new AuthServiceImpl(userRepository, sessionRepository);
        this.userService = new UserServiceImpl(userRepository);
        
        this.friendshipService = new FriendshipServiceImpl(
                friendRequestRepository, userRepository, notificationService);
        
        this.postService = new PostServiceImpl(
                postRepository, userRepository, friendshipService);
        
        this.newsfeedService = new NewsfeedServiceImpl(
                postRepository, userRepository, friendshipService);
        
        this.interactionService = new InteractionServiceImpl(
                postRepository, likeRepository, commentRepository, 
                userRepository, notificationService);

        // Register default notification observers
        registerDefaultObservers();
    }

    private void registerDefaultObservers() {
        notificationService.registerObserver(new InAppNotificationObserver(notificationRepository));
        notificationService.registerObserver(new EmailNotificationObserver(userRepository));
        notificationService.registerObserver(new PushNotificationObserver());
    }

    // ==================== Authentication ====================

    /**
     * Register a new user.
     */
    public User register(String name, String email, String password) {
        return authService.register(name, email, password);
    }

    /**
     * Login and get a session token.
     */
    public Session login(String email, String password) {
        return authService.login(email, password);
    }

    /**
     * Logout (invalidate session).
     */
    public void logout(String token) {
        authService.logout(token);
    }

    /**
     * Validate session and get user.
     */
    public User validateSession(String token) {
        return authService.validateSession(token);
    }

    // ==================== User Profile ====================

    /**
     * Get user by ID.
     */
    public User getUser(String userId) {
        return userService.getUser(userId).orElse(null);
    }

    /**
     * Update user profile.
     */
    public User updateProfile(String userId, String name, String bio, String interests) {
        return userService.updateProfile(userId, name, bio, interests);
    }

    /**
     * Update profile picture.
     */
    public User updateProfilePicture(String userId, String pictureUrl) {
        return userService.updateProfilePicture(userId, pictureUrl);
    }

    /**
     * Update privacy settings.
     */
    public User updatePrivacySettings(String userId, PrivacyLevel profilePrivacy, 
                                       PrivacyLevel defaultPostPrivacy) {
        return userService.updatePrivacySettings(userId, profilePrivacy, defaultPostPrivacy);
    }

    /**
     * Search users by name.
     */
    public List<User> searchUsers(String name) {
        return userService.searchUsers(name);
    }

    // ==================== Friendships ====================

    /**
     * Send a friend request.
     */
    public FriendRequest sendFriendRequest(String senderId, String receiverId) {
        return friendshipService.sendFriendRequest(senderId, receiverId);
    }

    /**
     * Accept a friend request.
     */
    public void acceptFriendRequest(String requestId, String userId) {
        friendshipService.acceptFriendRequest(requestId, userId);
    }

    /**
     * Decline a friend request.
     */
    public void declineFriendRequest(String requestId, String userId) {
        friendshipService.declineFriendRequest(requestId, userId);
    }

    /**
     * Get pending friend requests.
     */
    public List<FriendRequest> getPendingFriendRequests(String userId) {
        return friendshipService.getPendingRequests(userId);
    }

    /**
     * Get list of friends.
     */
    public List<User> getFriends(String userId) {
        return friendshipService.getFriends(userId);
    }

    /**
     * Check if two users are friends.
     */
    public boolean areFriends(String userId1, String userId2) {
        return friendshipService.areFriends(userId1, userId2);
    }

    /**
     * Unfriend a user.
     */
    public void unfriend(String userId, String friendId) {
        friendshipService.unfriend(userId, friendId);
    }

    // ==================== Posts ====================

    /**
     * Create a new post.
     */
    public Post createPost(String authorId, String content, PostType type, 
                           String mediaUrl, PrivacyLevel privacyLevel) {
        return postService.createPost(authorId, content, type, mediaUrl, privacyLevel);
    }

    /**
     * Create a simple text post.
     */
    public Post createTextPost(String authorId, String content) {
        return postService.createTextPost(authorId, content);
    }

    /**
     * Get a post by ID.
     */
    public Post getPost(String postId, String viewerId) {
        return postService.getPost(postId, viewerId);
    }

    /**
     * Get posts by user.
     */
    public List<Post> getPostsByUser(String userId, String viewerId) {
        return postService.getPostsByUser(userId, viewerId);
    }

    /**
     * Update a post.
     */
    public Post updatePost(String postId, String userId, String content) {
        return postService.updatePost(postId, userId, content);
    }

    /**
     * Delete a post.
     */
    public void deletePost(String postId, String userId) {
        postService.deletePost(postId, userId);
    }

    // ==================== Newsfeed ====================

    /**
     * Get newsfeed for a user.
     */
    public List<Post> getNewsfeed(String userId) {
        return newsfeedService.getNewsfeed(userId);
    }

    /**
     * Get newsfeed with custom limit.
     */
    public List<Post> getNewsfeed(String userId, int limit) {
        return newsfeedService.getNewsfeed(userId, limit);
    }

    /**
     * Set the newsfeed generation strategy.
     */
    public void setNewsfeedStrategy(NewsfeedStrategy strategy) {
        newsfeedService.setNewsfeedStrategy(strategy);
    }

    // ==================== Interactions ====================

    /**
     * Like a post.
     */
    public Like likePost(String postId, String userId) {
        return interactionService.likePost(postId, userId);
    }

    /**
     * Unlike a post.
     */
    public void unlikePost(String postId, String userId) {
        interactionService.unlikePost(postId, userId);
    }

    /**
     * Check if user has liked a post.
     */
    public boolean hasLiked(String postId, String userId) {
        return interactionService.hasLiked(postId, userId);
    }

    /**
     * Get users who liked a post.
     */
    public List<User> getUsersWhoLiked(String postId) {
        return interactionService.getUsersWhoLiked(postId);
    }

    /**
     * Add a comment to a post.
     */
    public Comment addComment(String postId, String authorId, String content) {
        return interactionService.addComment(postId, authorId, content);
    }

    /**
     * Get comments on a post.
     */
    public List<Comment> getComments(String postId) {
        return interactionService.getComments(postId);
    }

    /**
     * Delete a comment.
     */
    public void deleteComment(String commentId, String userId) {
        interactionService.deleteComment(commentId, userId);
    }

    // ==================== Notifications ====================

    /**
     * Get all notifications for a user.
     */
    public List<Notification> getNotifications(String userId) {
        return notificationService.getNotifications(userId);
    }

    /**
     * Get unread notifications.
     */
    public List<Notification> getUnreadNotifications(String userId) {
        return notificationService.getUnreadNotifications(userId);
    }

    /**
     * Get unread notification count.
     */
    public int getUnreadNotificationCount(String userId) {
        return notificationService.getUnreadCount(userId);
    }

    /**
     * Mark notification as read.
     */
    public void markNotificationAsRead(String notificationId) {
        notificationService.markAsRead(notificationId);
    }

    /**
     * Mark all notifications as read.
     */
    public void markAllNotificationsAsRead(String userId) {
        notificationService.markAllAsRead(userId);
    }

    /**
     * Register a custom notification observer.
     */
    public void registerNotificationObserver(NotificationObserver observer) {
        notificationService.registerObserver(observer);
    }
}



