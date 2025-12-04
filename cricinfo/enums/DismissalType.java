package cricinfo.enums;

/**
 * Represents different ways a batsman can be dismissed.
 */
public enum DismissalType {
    BOWLED("Bowled", true),
    CAUGHT("Caught", true),
    LBW("Leg Before Wicket", true),
    RUN_OUT("Run Out", false),
    STUMPED("Stumped", true),
    HIT_WICKET("Hit Wicket", true),
    HANDLED_BALL("Handled the Ball", false),
    OBSTRUCTING_FIELD("Obstructing the Field", false),
    TIMED_OUT("Timed Out", false),
    RETIRED_HURT("Retired Hurt", false),
    RETIRED_OUT("Retired Out", false),
    NOT_OUT("Not Out", false);

    private final String displayName;
    private final boolean creditedToBowler;

    DismissalType(String displayName, boolean creditedToBowler) {
        this.displayName = displayName;
        this.creditedToBowler = creditedToBowler;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isCreditedToBowler() {
        return creditedToBowler;
    }
}



