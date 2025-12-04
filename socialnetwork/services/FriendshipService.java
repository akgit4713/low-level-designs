package socialnetwork.services;

import socialnetwork.models.FriendRequest;
import socialnetwork.models.User;

import java.util.List;

/**
 * Service interface for friendship operations.
 */
public interface FriendshipService {
    
    /**
     * Send a friend request.
     */
    FriendRequest sendFriendRequest(String senderId, String receiverId);
    
    /**
     * Accept a friend request.
     */
    void acceptFriendRequest(String requestId, String userId);
    
    /**
     * Decline a friend request.
     */
    void declineFriendRequest(String requestId, String userId);
    
    /**
     * Get pending friend requests for a user.
     */
    List<FriendRequest> getPendingRequests(String userId);
    
    /**
     * Get list of friends for a user.
     */
    List<User> getFriends(String userId);
    
    /**
     * Get friend IDs for a user.
     */
    List<String> getFriendIds(String userId);
    
    /**
     * Check if two users are friends.
     */
    boolean areFriends(String userId1, String userId2);
    
    /**
     * Unfriend a user.
     */
    void unfriend(String userId, String friendId);
    
    /**
     * Block a user.
     */
    void blockUser(String userId, String blockedUserId);
}



