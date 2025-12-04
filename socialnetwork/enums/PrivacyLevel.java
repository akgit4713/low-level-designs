package socialnetwork.enums;

/**
 * Privacy levels for posts and profile information.
 */
public enum PrivacyLevel {
    PUBLIC("Visible to everyone"),
    FRIENDS_ONLY("Visible to friends only"),
    PRIVATE("Visible only to you");

    private final String description;

    PrivacyLevel(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



