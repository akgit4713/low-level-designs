package bookmyshow.enums;

/**
 * Types of seats available in a theater screen.
 * Each type has different pricing and comfort level.
 */
public enum SeatType {
    REGULAR("Regular", 1.0),
    PREMIUM("Premium", 1.5),
    RECLINER("Recliner", 2.0),
    VIP("VIP", 2.5),
    WHEELCHAIR("Wheelchair Accessible", 1.0);

    private final String displayName;
    private final double priceMultiplier;

    SeatType(String displayName, double priceMultiplier) {
        this.displayName = displayName;
        this.priceMultiplier = priceMultiplier;
    }

    public String getDisplayName() {
        return displayName;
    }

    public double getPriceMultiplier() {
        return priceMultiplier;
    }
}



