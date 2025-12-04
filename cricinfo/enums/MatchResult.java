package cricinfo.enums;

/**
 * Represents the result of a completed match.
 */
public enum MatchResult {
    TEAM1_WIN("Team 1 Won"),
    TEAM2_WIN("Team 2 Won"),
    TIE("Match Tied"),
    DRAW("Match Drawn"),
    NO_RESULT("No Result"),
    SUPER_OVER("Decided by Super Over");

    private final String displayName;

    MatchResult(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



