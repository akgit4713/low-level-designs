package trafficsignal.models;

import trafficsignal.enums.Direction;
import trafficsignal.enums.TrafficDensity;

import java.util.UUID;

/**
 * Represents a road at an intersection.
 */
public class Road {
    
    private final String id;
    private final String name;
    private final Direction direction;
    private final TrafficSignal signal;
    private TrafficDensity currentDensity;
    private int vehicleCount;

    public Road(String name, Direction direction) {
        this.id = UUID.randomUUID().toString();
        this.name = name;
        this.direction = direction;
        this.signal = new TrafficSignal(id);
        this.currentDensity = TrafficDensity.NORMAL;
        this.vehicleCount = 0;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Direction getDirection() {
        return direction;
    }

    public TrafficSignal getSignal() {
        return signal;
    }

    public TrafficDensity getCurrentDensity() {
        return currentDensity;
    }

    public void setCurrentDensity(TrafficDensity density) {
        this.currentDensity = density;
    }

    public int getVehicleCount() {
        return vehicleCount;
    }

    public void setVehicleCount(int count) {
        this.vehicleCount = count;
        updateDensity();
    }

    public void incrementVehicleCount() {
        this.vehicleCount++;
        updateDensity();
    }

    public void decrementVehicleCount() {
        if (this.vehicleCount > 0) {
            this.vehicleCount--;
            updateDensity();
        }
    }

    private void updateDensity() {
        // Simple density calculation based on vehicle count
        if (vehicleCount < 5) {
            currentDensity = TrafficDensity.LOW;
        } else if (vehicleCount < 15) {
            currentDensity = TrafficDensity.NORMAL;
        } else if (vehicleCount < 30) {
            currentDensity = TrafficDensity.HIGH;
        } else {
            currentDensity = TrafficDensity.VERY_HIGH;
        }
    }

    @Override
    public String toString() {
        return String.format("Road[name=%s, direction=%s, signal=%s, density=%s]", 
            name, direction, signal.getCurrentColor(), currentDensity);
    }
}



