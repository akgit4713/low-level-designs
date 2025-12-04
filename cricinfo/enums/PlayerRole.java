package cricinfo.enums;

/**
 * Represents a player's primary role in the team.
 */
public enum PlayerRole {
    BATSMAN("Batsman"),
    BOWLER("Bowler"),
    ALL_ROUNDER("All-rounder"),
    WICKET_KEEPER("Wicket-keeper"),
    WICKET_KEEPER_BATSMAN("Wicket-keeper Batsman");

    private final String displayName;

    PlayerRole(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}



