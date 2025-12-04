package parkinglot.strategies.allocation;

import parkinglot.models.Level;
import parkinglot.models.SpotResult;
import parkinglot.models.Vehicle;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for allocating parking spots to vehicles.
 * Implements the Strategy Pattern to allow different allocation algorithms.
 * 
 * Extension point: Implement this interface to add new allocation strategies
 * (e.g., nearest to entrance, spread across levels, prefer specific floors).
 */
public interface SpotAllocationStrategy {
    
    /**
     * Finds an appropriate parking spot for the given vehicle.
     * 
     * @param levels List of all levels in the parking lot
     * @param vehicle The vehicle that needs parking
     * @return Optional containing SpotResult if found, empty otherwise
     */
    Optional<SpotResult> findSpot(List<Level> levels, Vehicle vehicle);
    
    /**
     * Gets a description of this allocation strategy.
     */
    String getDescription();
}



