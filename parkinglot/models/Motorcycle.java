package parkinglot.models;

import parkinglot.enums.VehicleType;

/**
 * Represents a motorcycle vehicle type.
 */
public class Motorcycle extends Vehicle {
    
    public Motorcycle(String licensePlate) {
        super(licensePlate, VehicleType.MOTORCYCLE);
    }
}



