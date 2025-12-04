package parkinglot.strategies.allocation;

import parkinglot.models.Level;
import parkinglot.models.ParkingSpot;
import parkinglot.models.SpotResult;
import parkinglot.models.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Default spot allocation strategy that finds the first available spot.
 * Searches levels sequentially from lowest to highest floor.
 */
public class FirstAvailableStrategy implements SpotAllocationStrategy {

    @Override
    public Optional<SpotResult> findSpot(List<Level> levels, Vehicle vehicle) {
        for (Level level : levels) {
            for (ParkingSpot spot : level.getParkingSpots()) {
                if (spot.canFitVehicle(vehicle) && spot.isAvailable()) {
                    return Optional.of(new SpotResult(level, spot));
                }
            }
        }
        return Optional.empty();
    }

    @Override
    public String getDescription() {
        return "First available spot from lowest level";
    }
}



