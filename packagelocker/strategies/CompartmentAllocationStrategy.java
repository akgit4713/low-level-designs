package packagelocker.strategies;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;

import java.util.List;
import java.util.Optional;

/**
 * Strategy interface for compartment allocation algorithms.
 * Implements Strategy Pattern for flexible allocation logic.
 */
public interface CompartmentAllocationStrategy {
    
    /**
     * Allocates a compartment for the given package size.
     * 
     * @param availableCompartments list of available compartments to choose from
     * @param requestedSize the size of compartment requested
     * @return an Optional containing the selected compartment, or empty if none available
     */
    Optional<Compartment> allocate(List<Compartment> availableCompartments, 
                                    CompartmentSize requestedSize);
    
    /**
     * Returns the name of this allocation strategy.
     */
    String getStrategyName();
}
