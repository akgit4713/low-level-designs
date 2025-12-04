package cricinfo.enums;

/**
 * Represents different types of deliveries.
 */
public enum BallType {
    LEGAL("Legal Delivery"),
    WIDE("Wide"),
    NO_BALL("No Ball"),
    LEG_BYE("Leg Bye"),
    BYE("Bye"),
    DEAD_BALL("Dead Ball");

    private final String displayName;

    BallType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isExtra() {
        return this == WIDE || this == NO_BALL || this == LEG_BYE || this == BYE;
    }

    public boolean incrementsBallCount() {
        return this != WIDE && this != NO_BALL && this != DEAD_BALL;
    }
}



