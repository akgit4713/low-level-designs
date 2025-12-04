package airline.enums;

/**
 * Types of baggage.
 */
public enum BaggageType {
    CABIN("Cabin Baggage", 7.0),
    CHECKED("Checked Baggage", 23.0);

    private final String displayName;
    private final double defaultWeightLimit; // in kg

    BaggageType(String displayName, double defaultWeightLimit) {
        this.displayName = displayName;
        this.defaultWeightLimit = defaultWeightLimit;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getDefaultWeightLimit() {
        return defaultWeightLimit;
    }
}



