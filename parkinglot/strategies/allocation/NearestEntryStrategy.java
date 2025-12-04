package parkinglot.strategies.allocation;

import parkinglot.models.Level;
import parkinglot.models.ParkingSpot;
import parkinglot.models.SpotResult;
import parkinglot.models.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Spot allocation strategy that finds spots nearest to entry points.
 * Prefers spots with lower spot numbers (closer to entrance) on lower levels.
 */
public class NearestEntryStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<SpotResult> findSpot(List<Level> levels, Vehicle vehicle) {
        SpotResult nearestResult = null;
        int nearestScore = Integer.MAX_VALUE;
        
        for (Level level : levels) {
            for (ParkingSpot spot : level.getParkingSpots()) {
                if (spot.canFitVehicle(vehicle) && spot.isAvailable()) {
                    // Score = level * 100 + spot number (lower is better)
                    int score = level.getFloorNumber() * 100 + spot.getSpotNumber();
                    if (score < nearestScore) {
                        nearestScore = score;
                        nearestResult = new SpotResult(level, spot);
                    }
                }
            }
        }
        
        return Optional.ofNullable(nearestResult);
    }

    @Override
    public String getDescription() {
        return "Nearest spot to entry point (prefers lower levels and spot numbers)";
    }
}



