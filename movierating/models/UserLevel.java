package movierating.models;

/**
 * Enum representing different user levels in the movie rating system.
 * Each level has an associated weight multiplier that affects the impact of their ratings.
 * 
 * Open/Closed Principle: New levels can be added without modifying existing code.
 */
public enum UserLevel {
    NOVICE(1, "Novice", 0),
    INTERMEDIATE(2, "Intermediate", 10),
    PRO(3, "Pro", 50),
    EXPERT(4, "Expert", 100),
    MASTER(5, "Master", 200);

    private final int weightMultiplier;
    private final String displayName;
    private final int minRatingsRequired;

    UserLevel(int weightMultiplier, String displayName, int minRatingsRequired) {
        this.weightMultiplier = weightMultiplier;
        this.displayName = displayName;
        this.minRatingsRequired = minRatingsRequired;
    }

    public int getWeightMultiplier() {
        return weightMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getMinRatingsRequired() {
        return minRatingsRequired;
    }

    /**
     * Get the next level in the hierarchy.
     * @return Next level or null if already at max level
     */
    public UserLevel getNextLevel() {
        UserLevel[] levels = UserLevel.values();
        int nextOrdinal = this.ordinal() + 1;
        return nextOrdinal < levels.length ? levels[nextOrdinal] : null;
    }

    /**
     * Get the previous level in the hierarchy.
     * @return Previous level or null if already at min level
     */
    public UserLevel getPreviousLevel() {
        int prevOrdinal = this.ordinal() - 1;
        return prevOrdinal >= 0 ? UserLevel.values()[prevOrdinal] : null;
    }

    @Override
    public String toString() {
        return displayName + " (Weight: " + weightMultiplier + "x)";
    }
}


