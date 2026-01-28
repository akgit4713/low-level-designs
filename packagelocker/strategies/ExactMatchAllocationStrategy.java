package packagelocker.strategies;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;

import java.util.List;
import java.util.Optional;

/**
 * Allocates compartments that exactly match the requested size.
 * This is the simplest and most efficient strategy.
 */
public class ExactMatchAllocationStrategy implements CompartmentAllocationStrategy {
    
    @Override
    public Optional<Compartment> allocate(List<Compartment> availableCompartments, 
                                          CompartmentSize requestedSize) {
        return availableCompartments.stream()
                .filter(Compartment::isAvailable)
                .filter(c -> c.getSize() == requestedSize)
                .findFirst();
    }

    @Override
    public String getStrategyName() {
        return "Exact Match";
    }
}
