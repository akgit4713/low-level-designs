package parkinglot.models;

import parkinglot.enums.VehicleType;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a level/floor in the parking lot.
 * Each level contains multiple parking spots of different types.
 */
public class Level {
    private final int floorNumber;
    private final List<ParkingSpot> parkingSpots;

    public Level(int floorNumber, int numMotorcycleSpots, int numCarSpots, int numTruckSpots) {
        this.floorNumber = floorNumber;
        this.parkingSpots = new ArrayList<>();
        
        int spotNumber = 1;
        
        // Create motorcycle spots
        for (int i = 0; i < numMotorcycleSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, VehicleType.MOTORCYCLE));
        }
        
        // Create car spots
        for (int i = 0; i < numCarSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, VehicleType.CAR));
        }
        
        // Create truck spots
        for (int i = 0; i < numTruckSpots; i++) {
            parkingSpots.add(new ParkingSpot(spotNumber++, VehicleType.TRUCK));
        }
    }

    public int getFloorNumber() {
        return floorNumber;
    }

    public List<ParkingSpot> getParkingSpots() {
        return new ArrayList<>(parkingSpots);
    }

    /**
     * Attempts to park a vehicle on this level.
     * @return the parking spot if successful, null otherwise
     */
    public synchronized ParkingSpot parkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (spot.canFitVehicle(vehicle) && spot.parkVehicle(vehicle)) {
                return spot;
            }
        }
        return null;
    }

    /**
     * Unparks a vehicle from this level.
     * @return true if vehicle was found and unparked, false otherwise
     */
    public synchronized boolean unparkVehicle(Vehicle vehicle) {
        for (ParkingSpot spot : parkingSpots) {
            if (!spot.isAvailable() && 
                spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate())) {
                spot.unparkVehicle();
                return true;
            }
        }
        return false;
    }

    /**
     * Gets the count of available spots for a specific vehicle type.
     */
    public int getAvailableSpotCount(VehicleType type) {
        return (int) parkingSpots.stream()
            .filter(spot -> spot.getSpotType() == type && spot.isAvailable())
            .count();
    }

    /**
     * Gets total available spots on this level.
     */
    public int getTotalAvailableSpots() {
        return (int) parkingSpots.stream()
            .filter(ParkingSpot::isAvailable)
            .count();
    }

    /**
     * Gets total spots on this level.
     */
    public int getTotalSpots() {
        return parkingSpots.size();
    }

    /**
     * Finds the spot where a specific vehicle is parked.
     */
    public ParkingSpot findVehicleSpot(Vehicle vehicle) {
        return parkingSpots.stream()
            .filter(spot -> !spot.isAvailable() && 
                    spot.getParkedVehicle().getLicensePlate().equals(vehicle.getLicensePlate()))
            .findFirst()
            .orElse(null);
    }

    @Override
    public String toString() {
        return String.format("Level[Floor %d, %d/%d spots available]", 
            floorNumber, getTotalAvailableSpots(), getTotalSpots());
    }
}



