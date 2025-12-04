package parkinglot.models;

/**
 * Represents the result of a spot allocation search.
 * Contains both the level and the spot found.
 */
public class SpotResult {
    private final Level level;
    private final ParkingSpot spot;

    public SpotResult(Level level, ParkingSpot spot) {
        this.level = level;
        this.spot = spot;
    }

    public Level getLevel() {
        return level;
    }

    public ParkingSpot getSpot() {
        return spot;
    }

    @Override
    public String toString() {
        return String.format("SpotResult[Level %d, Spot %d]", 
            level.getFloorNumber(), spot.getSpotNumber());
    }
}



