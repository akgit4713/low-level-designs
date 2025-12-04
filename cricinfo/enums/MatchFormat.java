package cricinfo.enums;

/**
 * Represents different cricket match formats.
 */
public enum MatchFormat {
    TEST("Test Match", 5, 90, false),
    ODI("One Day International", 1, 50, true),
    T20("Twenty20", 1, 20, true),
    T10("Ten10", 1, 10, true),
    HUNDRED("The Hundred", 1, 100, true), // 100 balls per innings
    FIRST_CLASS("First Class", 4, 90, false),
    LIST_A("List A", 1, 50, true);

    private final String displayName;
    private final int maxDays;
    private final int oversPerInnings;
    private final boolean limitedOvers;

    MatchFormat(String displayName, int maxDays, int oversPerInnings, boolean limitedOvers) {
        this.displayName = displayName;
        this.maxDays = maxDays;
        this.oversPerInnings = oversPerInnings;
        this.limitedOvers = limitedOvers;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMaxDays() {
        return maxDays;
    }

    public int getOversPerInnings() {
        return oversPerInnings;
    }

    public boolean isLimitedOvers() {
        return limitedOvers;
    }
}



