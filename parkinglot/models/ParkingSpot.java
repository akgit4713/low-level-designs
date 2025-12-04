package parkinglot.models;

import parkinglot.enums.VehicleType;

/**
 * Represents an individual parking spot in the parking lot.
 * Each spot is designed for a specific vehicle type.
 */
public class ParkingSpot {
    private final int spotNumber;
    private final VehicleType spotType;
    private Vehicle parkedVehicle;

    public ParkingSpot(int spotNumber, VehicleType spotType) {
        this.spotNumber = spotNumber;
        this.spotType = spotType;
        this.parkedVehicle = null;
    }

    public int getSpotNumber() {
        return spotNumber;
    }

    public VehicleType getSpotType() {
        return spotType;
    }

    public Vehicle getParkedVehicle() {
        return parkedVehicle;
    }

    /**
     * Checks if the spot is currently available.
     */
    public synchronized boolean isAvailable() {
        return parkedVehicle == null;
    }

    /**
     * Checks if a vehicle can fit in this spot.
     * A vehicle can fit if the spot type matches the vehicle type.
     */
    public boolean canFitVehicle(Vehicle vehicle) {
        return vehicle != null && spotType == vehicle.getType();
    }

    /**
     * Parks a vehicle in this spot.
     * @return true if parking was successful, false otherwise
     */
    public synchronized boolean parkVehicle(Vehicle vehicle) {
        if (!isAvailable() || !canFitVehicle(vehicle)) {
            return false;
        }
        this.parkedVehicle = vehicle;
        return true;
    }

    /**
     * Removes the vehicle from this spot.
     * @return the vehicle that was parked, or null if spot was empty
     */
    public synchronized Vehicle unparkVehicle() {
        Vehicle vehicle = this.parkedVehicle;
        this.parkedVehicle = null;
        return vehicle;
    }

    @Override
    public String toString() {
        return String.format("Spot[%d, %s, %s]", 
            spotNumber, 
            spotType, 
            isAvailable() ? "AVAILABLE" : "OCCUPIED");
    }
}



