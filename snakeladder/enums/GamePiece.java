package snakeladder.enums;

/**
 * Represents different game pieces that players can use.
 */
public enum GamePiece {
    RED("🔴", "Red"),
    BLUE("🔵", "Blue"),
    GREEN("🟢", "Green"),
    YELLOW("🟡", "Yellow"),
    PURPLE("🟣", "Purple"),
    ORANGE("🟠", "Orange"),
    WHITE("⚪", "White"),
    BLACK("⚫", "Black");

    private final String emoji;
    private final String displayName;

    GamePiece(String emoji, String displayName) {
        this.emoji = emoji;
        this.displayName = displayName;
    }

    public String getEmoji() {
        return emoji;
    }

    public String getDisplayName() {
        return displayName;
    }

    @Override
    public String toString() {
        return emoji + " " + displayName;
    }
}



