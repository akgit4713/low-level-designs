package ridesharing.enums;

public enum VehicleType {
    SEDAN(4, "Standard sedan"),
    SUV(6, "Sport utility vehicle"),
    LUXURY(4, "Luxury vehicle"),
    MINI(4, "Compact car");

    private final int capacity;
    private final String description;

    VehicleType(int capacity, String description) {
        this.capacity = capacity;
        this.description = description;
    }

    public int getCapacity() {
        return capacity;
    }

    public String getDescription() {
        return description;
    }
}



