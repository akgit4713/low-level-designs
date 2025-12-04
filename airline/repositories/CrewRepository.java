package airline.repositories;

import airline.enums.CrewRole;
import airline.models.Crew;

import java.util.List;

/**
 * Repository interface for Crew entities.
 */
public interface CrewRepository extends Repository<Crew, String> {
    
    /**
     * Finds a crew member by employee ID.
     */
    Crew findByEmployeeId(String employeeId);
    
    /**
     * Finds crew members by role.
     */
    List<Crew> findByRole(CrewRole role);
    
    /**
     * Finds available crew members.
     */
    List<Crew> findAvailable();
    
    /**
     * Finds available crew members by role.
     */
    List<Crew> findAvailableByRole(CrewRole role);
    
    /**
     * Finds crew members certified for an aircraft model.
     */
    List<Crew> findByCertification(String aircraftModel);
}



