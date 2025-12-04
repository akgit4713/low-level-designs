package cricinfo.enums;

/**
 * Represents a player's bowling style.
 */
public enum BowlingStyle {
    RIGHT_ARM_FAST("Right-arm fast"),
    RIGHT_ARM_MEDIUM("Right-arm medium"),
    RIGHT_ARM_OFF_BREAK("Right-arm off-break"),
    RIGHT_ARM_LEG_BREAK("Right-arm leg-break"),
    LEFT_ARM_FAST("Left-arm fast"),
    LEFT_ARM_MEDIUM("Left-arm medium"),
    LEFT_ARM_ORTHODOX("Left-arm orthodox"),
    LEFT_ARM_CHINAMAN("Left-arm chinaman"),
    SLOW_LEFT_ARM("Slow left-arm");

    private final String displayName;

    BowlingStyle(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



