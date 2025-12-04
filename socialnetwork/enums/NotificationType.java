package socialnetwork.enums;

/**
 * Types of notifications users can receive.
 */
public enum NotificationType {
    FRIEND_REQUEST("sent you a friend request"),
    FRIEND_ACCEPTED("accepted your friend request"),
    LIKE("liked your post"),
    COMMENT("commented on your post"),
    MENTION("mentioned you in a post"),
    POST("shared a new post");

    private final String messageTemplate;

    NotificationType(String messageTemplate) {
        this.messageTemplate = messageTemplate;
    }

    public String getMessageTemplate() {
        return messageTemplate;
    }

    public String formatMessage(String actorName) {
        return actorName + " " + messageTemplate;
    }
}



