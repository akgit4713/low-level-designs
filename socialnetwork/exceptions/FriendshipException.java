package socialnetwork.exceptions;

/**
 * Exception thrown for friendship-related errors.
 */
public class FriendshipException extends SocialNetworkException {
    
    public FriendshipException(String message) {
        super(message);
    }

    public static FriendshipException requestAlreadyExists() {
        return new FriendshipException("Friend request already exists between these users");
    }

    public static FriendshipException alreadyFriends() {
        return new FriendshipException("Users are already friends");
    }

    public static FriendshipException requestNotFound() {
        return new FriendshipException("Friend request not found");
    }

    public static FriendshipException cannotAddSelf() {
        return new FriendshipException("Cannot send friend request to yourself");
    }

    public static FriendshipException userBlocked() {
        return new FriendshipException("Cannot interact with this user");
    }
}



