package socialnetwork.factories;

import socialnetwork.enums.NotificationType;
import socialnetwork.models.Notification;
import socialnetwork.models.User;

/**
 * Factory for creating notifications with proper formatting.
 */
public class NotificationFactory {

    /**
     * Create a friend request notification.
     */
    public static Notification createFriendRequest(User receiver, User sender) {
        return Notification.builder()
                .userId(receiver.getId())
                .actorId(sender.getId())
                .type(NotificationType.FRIEND_REQUEST)
                .message(sender.getName() + " sent you a friend request")
                .build();
    }

    /**
     * Create a friend accepted notification.
     */
    public static Notification createFriendAccepted(User receiver, User accepter) {
        return Notification.builder()
                .userId(receiver.getId())
                .actorId(accepter.getId())
                .type(NotificationType.FRIEND_ACCEPTED)
                .message(accepter.getName() + " accepted your friend request")
                .build();
    }

    /**
     * Create a like notification.
     */
    public static Notification createLike(String postOwnerId, User liker, String postId) {
        return Notification.builder()
                .userId(postOwnerId)
                .actorId(liker.getId())
                .type(NotificationType.LIKE)
                .referenceId(postId)
                .message(liker.getName() + " liked your post")
                .build();
    }

    /**
     * Create a comment notification.
     */
    public static Notification createComment(String postOwnerId, User commenter, 
                                             String postId, String commentPreview) {
        return Notification.builder()
                .userId(postOwnerId)
                .actorId(commenter.getId())
                .type(NotificationType.COMMENT)
                .referenceId(postId)
                .message(commenter.getName() + " commented: \"" + truncate(commentPreview, 50) + "\"")
                .build();
    }

    /**
     * Create a mention notification.
     */
    public static Notification createMention(String mentionedUserId, User mentioner, 
                                             String postId) {
        return Notification.builder()
                .userId(mentionedUserId)
                .actorId(mentioner.getId())
                .type(NotificationType.MENTION)
                .referenceId(postId)
                .message(mentioner.getName() + " mentioned you in a post")
                .build();
    }

    private static String truncate(String text, int maxLength) {
        if (text == null) return "";
        if (text.length() <= maxLength) return text;
        return text.substring(0, maxLength - 3) + "...";
    }
}



