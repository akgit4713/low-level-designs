package carrental.enums;

/**
 * Represents the type/category of a car in the rental system.
 */
public enum CarType {
    SEDAN("Sedan", 1.0),
    SUV("SUV", 1.3),
    HATCHBACK("Hatchback", 0.9),
    LUXURY("Luxury", 2.0),
    SPORTS("Sports", 2.5),
    MINIVAN("Minivan", 1.4),
    CONVERTIBLE("Convertible", 1.8);

    private final String displayName;
    private final double priceMultiplier;

    CarType(String displayName, double priceMultiplier) {
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



