package socialnetwork.enums;

/**
 * Represents the account status of a user.
 */
public enum UserStatus {
    ACTIVE("Account is active"),
    SUSPENDED("Account is temporarily suspended"),
    DEACTIVATED("Account is deactivated by user"),
    DELETED("Account is permanently deleted");

    private final String description;

    UserStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



