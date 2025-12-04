package socialnetwork.enums;

/**
 * Represents the status of a friendship/connection request between two users.
 */
public enum FriendshipStatus {
    PENDING("Pending approval"),
    ACCEPTED("Friends"),
    DECLINED("Request declined"),
    BLOCKED("User blocked");

    private final String description;

    FriendshipStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



