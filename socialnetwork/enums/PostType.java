package socialnetwork.enums;

/**
 * Represents different types of posts a user can create.
 */
public enum PostType {
    TEXT("Text post"),
    IMAGE("Image post"),
    VIDEO("Video post"),
    LINK("Link share");

    private final String description;

    PostType(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



