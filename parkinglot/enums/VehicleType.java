package parkinglot.enums;

/**
 * Enumeration of vehicle types supported by the parking lot.
 * Each type has an associated size factor for spot compatibility.
 */
public enum VehicleType {
    MOTORCYCLE(1),
    CAR(2),
    TRUCK(3);

    private final int sizeFactor;

    VehicleType(int sizeFactor) {
        this.sizeFactor = sizeFactor;
    }

    public int getSizeFactor() {
        return sizeFactor;
    }
}



