package parkinglot.strategies.allocation;

import parkinglot.models.Level;
import parkinglot.models.ParkingSpot;
import parkinglot.models.SpotResult;
import parkinglot.models.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Spot allocation strategy that spreads vehicles across levels.
 * Prefers the level with the most available spots of the required type.
 * Helps balance utilization across the parking lot.
 */
public class SpreadOutStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<SpotResult> findSpot(List<Level> levels, Vehicle vehicle) {
        Level bestLevel = null;
        ParkingSpot bestSpot = null;
        int maxAvailable = -1;
        
        for (Level level : levels) {
            int availableCount = level.getAvailableSpotCount(vehicle.getType());
            
            if (availableCount > maxAvailable) {
                // Find first available spot on this level
                for (ParkingSpot spot : level.getParkingSpots()) {
                    if (spot.canFitVehicle(vehicle) && spot.isAvailable()) {
                        maxAvailable = availableCount;
                        bestLevel = level;
                        bestSpot = spot;
                        break;
                    }
                }
            }
        }
        
        if (bestLevel != null && bestSpot != null) {
            return Optional.of(new SpotResult(bestLevel, bestSpot));
        }
        
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "Spread vehicles across levels (prefers level with most available spots)";
    }
}



