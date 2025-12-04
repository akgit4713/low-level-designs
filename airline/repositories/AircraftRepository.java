package airline.repositories;

import airline.enums.AircraftStatus;
import airline.models.Aircraft;

import java.util.List;

/**
 * Repository interface for Aircraft entities.
 */
public interface AircraftRepository extends Repository<Aircraft, String> {
    
    /**
     * Finds an aircraft by registration number.
     */
    Aircraft findByRegistrationNumber(String registrationNumber);
    
    /**
     * Finds aircraft by status.
     */
    List<Aircraft> findByStatus(AircraftStatus status);
    
    /**
     * Finds available aircraft.
     */
    List<Aircraft> findAvailable();
    
    /**
     * Finds aircraft by model.
     */
    List<Aircraft> findByModel(String model);
}



