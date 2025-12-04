package parkinglot.factories;

import parkinglot.enums.VehicleType;
import parkinglot.models.*;

/**
 * Factory class for creating vehicle instances.
 * Encapsulates vehicle creation logic.
 */
public class VehicleFactory {
    
    /**
     * Creates a vehicle of the specified type.
     * 
     * @param type The type of vehicle to create
     * @param licensePlate The license plate number
     * @return A new Vehicle instance
     */
    public static Vehicle createVehicle(VehicleType type, String licensePlate) {
        return switch (type) {
            case MOTORCYCLE -> new Motorcycle(licensePlate);
            case CAR -> new Car(licensePlate);
            case TRUCK -> new Truck(licensePlate);
        };
    }
    
    /**
     * Creates a car with the given license plate.
     */
    public static Car createCar(String licensePlate) {
        return new Car(licensePlate);
    }
    
    /**
     * Creates a motorcycle with the given license plate.
     */
    public static Motorcycle createMotorcycle(String licensePlate) {
        return new Motorcycle(licensePlate);
    }
    
    /**
     * Creates a truck with the given license plate.
     */
    public static Truck createTruck(String licensePlate) {
        return new Truck(licensePlate);
    }
}



