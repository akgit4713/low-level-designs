package snakeladder.enums;

/**
 * Represents the current status of a game.
 */
public enum GameStatus {
    NOT_STARTED("Not Started"),
    IN_PROGRESS("In Progress"),
    FINISHED("Finished"),
    CANCELLED("Cancelled");

    private final String displayName;

    GameStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



