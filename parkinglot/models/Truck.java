package parkinglot.models;

import parkinglot.enums.VehicleType;

/**
 * Represents a truck vehicle type.
 */
public class Truck extends Vehicle {
    
    public Truck(String licensePlate) {
        super(licensePlate, VehicleType.TRUCK);
    }
}



