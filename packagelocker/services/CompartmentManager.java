package packagelocker.services;

import packagelocker.enums.CompartmentSize;
import packagelocker.exceptions.NoAvailableCompartmentException;
import packagelocker.models.Compartment;
import packagelocker.repositories.CompartmentRepository;
import packagelocker.strategies.CompartmentAllocationStrategy;

import java.util.List;
import java.util.Optional;

/**
 * Manages compartment allocation and release.
 * Uses Strategy pattern for allocation logic.
 */
public class CompartmentManager {
    
    private final CompartmentRepository compartmentRepository;
    private final CompartmentAllocationStrategy allocationStrategy;

    public CompartmentManager(CompartmentRepository compartmentRepository,
                              CompartmentAllocationStrategy allocationStrategy) {
        this.compartmentRepository = compartmentRepository;
        this.allocationStrategy = allocationStrategy;
    }

    /**
     * Allocates a compartment for the given package size.
     * 
     * @throws NoAvailableCompartmentException if no suitable compartment is available
     */
    public Compartment allocate(CompartmentSize size) {
        List<Compartment> allCompartments = compartmentRepository.findAll();
        
        Optional<Compartment> allocated = allocationStrategy.allocate(allCompartments, size);
        
        Compartment compartment = allocated.orElseThrow(
                () -> new NoAvailableCompartmentException(size));
        
        compartment.occupy();
        compartmentRepository.save(compartment);
        
        return compartment;
    }

    /**
     * Releases a compartment, making it available again.
     */
    public void release(String compartmentId) {
        compartmentRepository.findById(compartmentId)
                .ifPresent(compartment -> {
                    if (compartment.isOccupied()) {
                        compartment.release();
                        compartmentRepository.save(compartment);
                    }
                });
    }

    /**
     * Releases a compartment by its number.
     */
    public void releaseByNumber(int compartmentNumber) {
        compartmentRepository.findByNumber(compartmentNumber)
                .ifPresent(compartment -> {
                    if (compartment.isOccupied()) {
                        compartment.release();
                        compartmentRepository.save(compartment);
                    }
                });
    }

    /**
     * Finds a compartment by its ID.
     */
    public Optional<Compartment> findById(String compartmentId) {
        return compartmentRepository.findById(compartmentId);
    }

    /**
     * Finds a compartment by its number.
     */
    public Optional<Compartment> findByNumber(int number) {
        return compartmentRepository.findByNumber(number);
    }

    /**
     * Returns the count of available compartments for a given size.
     */
    public int getAvailableCount(CompartmentSize size) {
        return compartmentRepository.findAvailableBySize(size).size();
    }

    /**
     * Returns the total count of available compartments.
     */
    public int getTotalAvailableCount() {
        return compartmentRepository.findAvailable().size();
    }

    public String getAllocationStrategyName() {
        return allocationStrategy.getStrategyName();
    }
}
