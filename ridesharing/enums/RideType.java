package ridesharing.enums;

public enum RideType {
    REGULAR(1.0, "Regular ride with standard vehicles"),
    PREMIUM(1.5, "Premium ride with luxury vehicles"),
    POOL(0.7, "Shared ride with other passengers");

    private final double multiplier;
    private final String description;

    RideType(double multiplier, String description) {
        this.multiplier = multiplier;
        this.description = description;
    }

    public double getMultiplier() {
        return multiplier;
    }

    public String getDescription() {
        return description;
    }
}



