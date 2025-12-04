package socialnetwork.repositories.impl;

import socialnetwork.enums.FriendshipStatus;
import socialnetwork.models.FriendRequest;
import socialnetwork.repositories.FriendRequestRepository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of FriendRequestRepository.
 */
public class InMemoryFriendRequestRepository implements FriendRequestRepository {
    
    private final Map<String, FriendRequest> requests = new ConcurrentHashMap<>();

    @Override
    public FriendRequest save(FriendRequest request) {
        requests.put(request.getId(), request);
        return request;
    }

    @Override
    public Optional<FriendRequest> findById(String id) {
        return Optional.ofNullable(requests.get(id));
    }

    @Override
    public Optional<FriendRequest> findBySenderAndReceiver(String senderId, String receiverId) {
        return requests.values().stream()
                .filter(r -> r.getSenderId().equals(senderId) && r.getReceiverId().equals(receiverId))
                .findFirst();
    }

    @Override
    public Optional<FriendRequest> findByUsers(String userId1, String userId2) {
        return requests.values().stream()
                .filter(r -> 
                    (r.getSenderId().equals(userId1) && r.getReceiverId().equals(userId2)) ||
                    (r.getSenderId().equals(userId2) && r.getReceiverId().equals(userId1)))
                .findFirst();
    }

    @Override
    public List<FriendRequest> findByUserId(String userId) {
        return requests.values().stream()
                .filter(r -> r.involvesUser(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> findByUserIdAndStatus(String userId, FriendshipStatus status) {
        return requests.values().stream()
                .filter(r -> r.involvesUser(userId) && r.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> findPendingRequestsForUser(String userId) {
        return requests.values().stream()
                .filter(r -> r.getReceiverId().equals(userId) && r.isPending())
                .collect(Collectors.toList());
    }

    @Override
    public List<FriendRequest> findAcceptedFriendships(String userId) {
        return requests.values().stream()
                .filter(r -> r.involvesUser(userId) && r.isAccepted())
                .collect(Collectors.toList());
    }

    @Override
    public void delete(String id) {
        requests.remove(id);
    }
}



