package socialnetwork.services.impl;

import socialnetwork.enums.FriendshipStatus;
import socialnetwork.exceptions.FriendshipException;
import socialnetwork.exceptions.UnauthorizedException;
import socialnetwork.exceptions.UserNotFoundException;
import socialnetwork.factories.NotificationFactory;
import socialnetwork.models.FriendRequest;
import socialnetwork.models.User;
import socialnetwork.repositories.FriendRequestRepository;
import socialnetwork.repositories.UserRepository;
import socialnetwork.services.FriendshipService;
import socialnetwork.services.NotificationService;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of FriendshipService.
 * Handles friend requests and connections.
 */
public class FriendshipServiceImpl implements FriendshipService {
    
    private final FriendRequestRepository friendRequestRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public FriendshipServiceImpl(FriendRequestRepository friendRequestRepository,
                                  UserRepository userRepository,
                                  NotificationService notificationService) {
        this.friendRequestRepository = friendRequestRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public FriendRequest sendFriendRequest(String senderId, String receiverId) {
        if (senderId.equals(receiverId)) {
            throw FriendshipException.cannotAddSelf();
        }

        User sender = userRepository.findById(senderId)
                .orElseThrow(() -> new UserNotFoundException(senderId));
        User receiver = userRepository.findById(receiverId)
                .orElseThrow(() -> new UserNotFoundException(receiverId));

        // Check if request already exists
        friendRequestRepository.findByUsers(senderId, receiverId).ifPresent(existing -> {
            if (existing.getStatus() == FriendshipStatus.ACCEPTED) {
                throw FriendshipException.alreadyFriends();
            }
            if (existing.getStatus() == FriendshipStatus.BLOCKED) {
                throw FriendshipException.userBlocked();
            }
            if (existing.getStatus() == FriendshipStatus.PENDING) {
                throw FriendshipException.requestAlreadyExists();
            }
        });

        FriendRequest request = new FriendRequest(senderId, receiverId);
        friendRequestRepository.save(request);

        // Send notification
        notificationService.sendNotification(
                NotificationFactory.createFriendRequest(receiver, sender));

        return request;
    }

    @Override
    public void acceptFriendRequest(String requestId, String userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(FriendshipException::requestNotFound);

        if (!request.getReceiverId().equals(userId)) {
            throw new UnauthorizedException("Only the recipient can accept this request");
        }

        if (!request.isPending()) {
            throw new FriendshipException("Request is no longer pending");
        }

        request.accept();
        friendRequestRepository.save(request);

        // Notify the sender
        User accepter = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException(userId));
        User sender = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new UserNotFoundException(request.getSenderId()));

        notificationService.sendNotification(
                NotificationFactory.createFriendAccepted(sender, accepter));
    }

    @Override
    public void declineFriendRequest(String requestId, String userId) {
        FriendRequest request = friendRequestRepository.findById(requestId)
                .orElseThrow(FriendshipException::requestNotFound);

        if (!request.getReceiverId().equals(userId)) {
            throw new UnauthorizedException("Only the recipient can decline this request");
        }

        request.decline();
        friendRequestRepository.save(request);
    }

    @Override
    public List<FriendRequest> getPendingRequests(String userId) {
        return friendRequestRepository.findPendingRequestsForUser(userId);
    }

    @Override
    public List<User> getFriends(String userId) {
        List<String> friendIds = getFriendIds(userId);
        return userRepository.findByIds(friendIds);
    }

    @Override
    public List<String> getFriendIds(String userId) {
        return friendRequestRepository.findAcceptedFriendships(userId).stream()
                .map(request -> request.getOtherUser(userId))
                .collect(Collectors.toList());
    }

    @Override
    public boolean areFriends(String userId1, String userId2) {
        return friendRequestRepository.findByUsers(userId1, userId2)
                .map(FriendRequest::isAccepted)
                .orElse(false);
    }

    @Override
    public void unfriend(String userId, String friendId) {
        FriendRequest request = friendRequestRepository.findByUsers(userId, friendId)
                .orElseThrow(FriendshipException::requestNotFound);

        if (!request.isAccepted()) {
            throw new FriendshipException("Users are not friends");
        }

        friendRequestRepository.delete(request.getId());
    }

    @Override
    public void blockUser(String userId, String blockedUserId) {
        friendRequestRepository.findByUsers(userId, blockedUserId).ifPresent(request -> {
            request.block();
            friendRequestRepository.save(request);
        });

        // If no existing request, create a blocked one
        if (friendRequestRepository.findByUsers(userId, blockedUserId).isEmpty()) {
            FriendRequest blocked = new FriendRequest(userId, blockedUserId);
            blocked.block();
            friendRequestRepository.save(blocked);
        }
    }
}



