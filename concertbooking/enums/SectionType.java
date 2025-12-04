package concertbooking.enums;

/**
 * Types of seating sections in a venue
 */
public enum SectionType {
    VIP("VIP Section", 3.0),
    PLATINUM("Platinum Section", 2.5),
    GOLD("Gold Section", 2.0),
    SILVER("Silver Section", 1.5),
    GENERAL("General Admission", 1.0),
    BALCONY("Balcony", 1.2),
    STANDING("Standing Area", 0.8);

    private final String displayName;
    private final double priceMultiplier;

    SectionType(String displayName, double priceMultiplier) {
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



