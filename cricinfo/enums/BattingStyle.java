package cricinfo.enums;

/**
 * Represents a player's batting style.
 */
public enum BattingStyle {
    RIGHT_HANDED("Right-handed"),
    LEFT_HANDED("Left-handed");

    private final String displayName;

    BattingStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



