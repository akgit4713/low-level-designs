package socialnetwork.repositories;

import socialnetwork.enums.FriendshipStatus;
import socialnetwork.models.FriendRequest;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for FriendRequest data access.
 */
public interface FriendRequestRepository {
    
    FriendRequest save(FriendRequest request);
    
    Optional<FriendRequest> findById(String id);
    
    Optional<FriendRequest> findBySenderAndReceiver(String senderId, String receiverId);
    
    Optional<FriendRequest> findByUsers(String userId1, String userId2);
    
    List<FriendRequest> findByUserId(String userId);
    
    List<FriendRequest> findByUserIdAndStatus(String userId, FriendshipStatus status);
    
    List<FriendRequest> findPendingRequestsForUser(String userId);
    
    List<FriendRequest> findAcceptedFriendships(String userId);
    
    void delete(String id);
}



