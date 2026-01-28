package packagelocker.repositories;

import packagelocker.enums.CompartmentSize;
import packagelocker.models.Compartment;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for Compartment persistence.
 */
public interface CompartmentRepository {
    
    void save(Compartment compartment);
    
    Optional<Compartment> findById(String id);
    
    Optional<Compartment> findByNumber(int number);
    
    List<Compartment> findAll();
    
    List<Compartment> findBySize(CompartmentSize size);
    
    List<Compartment> findAvailable();
    
    List<Compartment> findAvailableBySize(CompartmentSize size);
}
