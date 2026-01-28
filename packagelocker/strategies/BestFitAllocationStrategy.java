package packagelocker.strategies;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

/**
 * Allocates the smallest compartment that can fit the package.
 * First tries exact match, then tries larger sizes.
 * This strategy minimizes wasted space.
 */
public class BestFitAllocationStrategy implements CompartmentAllocationStrategy {
    
    @Override
    public Optional<Compartment> allocate(List<Compartment> availableCompartments, 
                                          CompartmentSize requestedSize) {
        // Sort by capacity and find the smallest that fits
        return availableCompartments.stream()
                .filter(Compartment::isAvailable)
                .filter(c -> c.getSize().getCapacity() >= requestedSize.getCapacity())
                .min(Comparator.comparingInt(c -> c.getSize().getCapacity()));
    }

    @Override
    public String getStrategyName() {
        return "Best Fit";
    }
}
