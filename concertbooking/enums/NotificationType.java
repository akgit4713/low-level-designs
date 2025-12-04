package concertbooking.enums;

/**
 * Types of notifications
 */
public enum NotificationType {
    EMAIL("Email"),
    SMS("SMS"),
    PUSH("Push Notification"),
    ALL("All channels");

    private final String displayName;

    NotificationType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



