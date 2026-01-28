package packagelocker.enums;

/**
 * Represents the available sizes for locker compartments.
 */
public enum CompartmentSize {
    SMALL("Small", 1),
    MEDIUM("Medium", 2),
    LARGE("Large", 3);

    private final String displayName;
    private final int capacity;

    CompartmentSize(String displayName, int capacity) {
        this.displayName = displayName;
        this.capacity = capacity;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getCapacity() {
        return capacity;
    }
}
