package packagelocker.enums;

/**
 * Represents the status of an access token.
 */
public enum TokenStatus {
    ACTIVE("Active - Token is valid and can be used"),
    USED("Used - Token has already been used for retrieval"),
    EXPIRED("Expired - Token validity period has passed");

    private final String description;

    TokenStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}
