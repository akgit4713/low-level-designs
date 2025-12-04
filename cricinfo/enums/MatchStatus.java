package cricinfo.enums;

/**
 * Represents the current status of a cricket match.
 */
public enum MatchStatus {
    SCHEDULED("Match is scheduled"),
    LIVE("Match is in progress"),
    INNINGS_BREAK("Break between innings"),
    DRINKS_BREAK("Drinks break"),
    LUNCH_BREAK("Lunch break"),
    TEA_BREAK("Tea break"),
    RAIN_DELAY("Match delayed due to rain"),
    COMPLETED("Match has completed"),
    ABANDONED("Match has been abandoned"),
    CANCELLED("Match has been cancelled"),
    POSTPONED("Match has been postponed");

    private final String description;

    MatchStatus(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}



